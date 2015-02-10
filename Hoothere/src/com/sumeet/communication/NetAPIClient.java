package com.sumeet.communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sumeet.callback.NetAPICallBack;

public class NetAPIClient {
	public static final int MediaTypePhoto = 0;
	public static final int MediaTypeVideo = 1;
	public static final int MediaTypeAudio = 2;
	
	private static NetAPIClient mSharedInstance;
	private AsyncHttpClient mClient;
	private Context mCxt;
	private RequestQueue mQueue;
	    	
    private NetAPIClient(Context cxt) {
    	mCxt = cxt;
    	mClient = new AsyncHttpClient();
    	mQueue = Volley.newRequestQueue(mCxt);
    }

    public static synchronized NetAPIClient sharedInstance(Context cxt) {
        
    	if (mSharedInstance == null) {
        	mSharedInstance = new NetAPIClient(cxt);
        }
        
        return mSharedInstance;
    }

    //------------ communication methods ---------------//
    public void jsonRequestByGET(String serviceURL, HashMap<String, String> param, NetAPICallBack callback) {
    	
    	final NetAPICallBack apiCallback = callback;
    	
        mClient.get(serviceURL, new RequestParams(param), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseObj) {

                Log.d("netapiSuccess : ", Integer.toString(statusCode));

                apiCallback.succeed(responseObj);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                Log.d("netapiFailure : ", Integer.toString(statusCode));

                apiCallback.failed(errorResponse);
            }
        });
    }
    
    public void jsonRequestByPOST(String serviceURL, HashMap<String, String> param, NetAPICallBack callback) {
    	
    	final NetAPICallBack apiCallback = callback;
    	mClient.post(mCxt, serviceURL, null, new RequestParams(param), "application/json", new JsonHttpResponseHandler() {
    		
    		@Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
    			apiCallback.succeed(response);
    		}
    		
    		@Override
    		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
    			apiCallback.failed(errorResponse);
    		}
    	});   	
    }

    public void jsonRequestByPOSTWithContentType(String serviceURL, byte[] avatarData, String contentType, NetAPICallBack callback) {
    	
    	final NetAPICallBack apiCallback = callback;
    	mClient.addHeader("Content-Type", contentType);
    	StringBuilder sb = new StringBuilder();
    	String boundary = "---------------------------14737809831466499882746641449";
    	sb.append(String.format("--%s\r\n", boundary));
    	sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"XXX.png\"\r\n");
    	sb.append("Content-Type: multipart/form-data\r\n\r\n");
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	
    	try {
			baos.write(sb.toString().getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    		baos.write(avatarData, baos.size(), avatarData.length);
    	sb.append("\r\n");
    	sb.append(String.format("--%s--\r\n", boundary));

    	StringEntity entity = null;
		try {
			entity = new StringEntity(sb.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	mClient.post(mCxt, serviceURL, entity, contentType, new JsonHttpResponseHandler() {
//    	mClient.post(mCxt, serviceURL, new RequestParams(param), new JsonHttpResponseHandler() {
    		
    		@Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                
    			Log.d("netapiSuccess : ", Integer.toString(statusCode));
    			
    			apiCallback.succeed(response);
    		}
    		
    		@Override
    		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
    			
    			Log.d("netapiFailure : ", Integer.toString(statusCode));
    			
    			apiCallback.failed(errorResponse);
    		}
    	});   	
    }
    
    public void sendImageDataViaHttpPost(final String serviceURL, final byte[] avatarData, final NetAPICallBack callback) {
    	
    	new Thread(){
    		@Override
    		public void run(){
    	    	String boundary = "---------------------------14737809831466499882746641449";
    	    	String twoHyphens = "--";
    	    	String lineEnd = "\r\n";
    	    	
    	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	    	DataOutputStream dos = new DataOutputStream(baos);

    		   	try {
    				dos.writeBytes(twoHyphens + boundary + lineEnd);
    			   	dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"XXX.png\"" + lineEnd); 
    			   	dos.writeBytes("Content-Type: multipart/form-data\r\n\r\n");
    			   	dos.write(avatarData);
    			   	dos.writeBytes(lineEnd); 
    			   	dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd); 
    			   	dos.flush(); 
    			   	dos.close();
    			} catch (IOException e1) {
    				e1.printStackTrace();
    			} catch (Exception e) {
    				e.printStackTrace();
    				callback.failed((JSONObject)null);
				} 

    		   	ByteArrayInputStream content = new ByteArrayInputStream(baos.toByteArray());
    		   	BasicHttpEntity entity = new BasicHttpEntity();
    		   	entity.setContent(content);

    		   	HttpPost httpPost = new HttpPost(URI.create(serviceURL));
    		   	httpPost.addHeader("Content-Type", "multipart/form-data; boundary="+boundary);

    		   	httpPost.setEntity(entity);
    		   	HttpClient httpClient = new DefaultHttpClient();
    		   	try {
     				httpClient.execute(httpPost);
    			} catch (ClientProtocolException e) {
    				e.printStackTrace();
    				if (callback != null) callback.failed((JSONObject)null);
    			} catch (IOException e) {
    				e.printStackTrace();
    				if (callback != null) callback.failed((JSONObject)null);
    			}catch (Exception e) {
    				e.printStackTrace();
    				if (callback != null) callback.failed((JSONObject)null);
				}
    		}
    	}.start();
    }
    
    public void sendImageDataViaHttpPostWithCallBack(final String serviceURL, final byte[] avatarData, final NetAPICallBack callback) {
    	
    	new Thread(){
    		@Override
    		public void run(){
    	    	String boundary = "---------------------------14737809831466499882746641449";
    	    	String twoHyphens = "--";
    	    	String lineEnd = "\r\n";
    	    	
    	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	    	DataOutputStream dos = new DataOutputStream(baos);

    		   	try {
    				dos.writeBytes(twoHyphens + boundary + lineEnd);
    			   	dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"XXX.png\"" + lineEnd); 
    			   	dos.writeBytes("Content-Type: multipart/form-data\r\n\r\n");
    			   	dos.write(avatarData);
    			   	dos.writeBytes(lineEnd); 
    			   	dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd); 
    			   	dos.flush(); 
    			   	dos.close();
    			} catch (IOException e1) {
    				e1.printStackTrace();
    			} catch (Exception e) {
    				e.printStackTrace();
    				callback.failed((JSONObject)null);
				} 

    		   	ByteArrayInputStream content = new ByteArrayInputStream(baos.toByteArray());
    		   	BasicHttpEntity entity = new BasicHttpEntity();
    		   	entity.setContent(content);

    		   	HttpPost httpPost = new HttpPost(URI.create(serviceURL));
    		   	httpPost.addHeader("Content-Type", "multipart/form-data; boundary="+boundary);
    		   	httpPost.addHeader("accept", "application/json");
    		   	httpPost.setEntity(entity);
    		   	HttpClient httpClient = new DefaultHttpClient();
    		   	try {
     				httpClient.execute(httpPost);
    	            if (callback != null) callback.succeed(null);
    			} catch (ClientProtocolException e) {
    				callback.failed((JSONObject)null);
    				e.printStackTrace();
    			} catch (IOException e) {
    				callback.failed((JSONObject)null);
    				e.printStackTrace();
    			}catch (Exception e) {
    				e.printStackTrace();
    				callback.failed((JSONObject)null);
				}
    		}
    	}.start();
    }
    
    public void jsonRequestByPOSTWithVolley(final String serviceURL, final HashMap <String, Object> param, final NetAPICallBack callback){
    	JSONObject jsParam = (param != null) ? new JSONObject(param) : null;
    	
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, serviceURL, jsParam, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (callback != null) callback.succeed(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (callback != null) callback.failed(error);
			}
		});
		mQueue.add(jsObjRequest);
    }
    
//    public void jsonRequestByPOSTWithVolley(final String serviceURL, final JSONObject param, final NetAPICallBack callback){
//    	
//		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, serviceURL, param, new Response.Listener<JSONObject>() {
//			@Override
//			public void onResponse(JSONObject response) {
//				if (callback != null) callback.succeed(response);
//			}
//		}, new Response.ErrorListener() {
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				if (callback != null) callback.failed(error);
//			}
//		});
//		mQueue.add(jsObjRequest);
//    }

    public void jsonRequestByGETWithVolley(final String serviceURL, final HashMap <String, String> param, final NetAPICallBack callback){
    	JSONObject jsParam = (param != null) ? new JSONObject(param) : null;
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, serviceURL, jsParam, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (callback != null) callback.succeed(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (callback != null) callback.failed(error);
			}
		}){
			@Override
			protected Map<String, String> getParams(){
				return param;
			}
		};
		mQueue.add(jsObjRequest);
    }
    
    public void jsonRequestByPUTWithVolley(final String serviceURL, final HashMap <String, String> param, final NetAPICallBack callback){
    	JSONObject jsParam = (param != null) ? new JSONObject(param) : null;
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, serviceURL, jsParam, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (callback != null) callback.succeed(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (callback != null) callback.failed(error);
			}
		}){
			@Override
			protected Map<String, String> getParams(){
				return param;
			}
		};
		
		mQueue.add(jsObjRequest);
    }
    
    public void jsonRequestByDELETEWithVolley(final String serviceURL, final HashMap <String, String> param, final NetAPICallBack callback){
    	JSONObject jsParam = (param != null) ? new JSONObject(param) : null;
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.DELETE, serviceURL, jsParam, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (callback != null) callback.succeed(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (callback != null) callback.failed(error);
			}
		}){
			@Override
			protected Map<String, String> getParams(){
				return param;
			}
		};
		mQueue.add(jsObjRequest);
    }

    public void jsonRequestByPOSTWithMediaData(String serviceURL, int media_type, byte[] media_data, NetAPICallBack callback) {

        final NetAPICallBack apiCallback = callback;

        RequestParams requestParam = new RequestParams();

        if (media_data != null) {

            String strKey;
            String strFileName;

            switch (media_type) {

                case MediaTypePhoto:
                    strKey = "file";
                    strFileName = "XXX.jpeg";
                    break;

                case MediaTypeAudio:
                    strKey = "audio";
                    strFileName = "audio.mp3";
                    break;

                case MediaTypeVideo:
                    strKey = "video";
                    strFileName = "video.mp4";
                    break;

                default:
                    strKey = "photo";
                    strFileName = "photo.jpeg";
                    break;
            }
            requestParam.put(strKey, new ByteArrayInputStream(media_data), strFileName);
        }
        mClient.post(mCxt, serviceURL, requestParam, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("netapiSuccess : ", Integer.toString(statusCode));

                apiCallback.succeed(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                Log.d("netapiFailure : ", Integer.toString(statusCode));

                apiCallback.failed(errorResponse);
            }
        });
    }

}

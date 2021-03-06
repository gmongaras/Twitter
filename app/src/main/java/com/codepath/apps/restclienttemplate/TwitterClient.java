package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.MenuItem;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;       // Change this inside apikey.properties
	public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET; // Change this inside apikey.properties

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				null,  // OAuth2 scope, null for OAuth1
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	// Send a request to get the home timeline
	public void getHomeTimeline(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", 1); // Parameters from https://developer.twitter.com/en/docs/twitter-api/v1/tweets/timelines/api-reference/get-statuses-home_timeline
		params.put("tweet_mode", "extended");
		client.get(apiUrl, params, handler);
	}

	// Send a request to load more tweets
	public void getTweets(String max_id, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("max_id", max_id);
		params.put("tweet_mode", "extended");
		client.get(apiUrl, params, handler);
	}

	// Given the tweet to publish and a handler, publish a new tweet
	public void publishTweet(String tweetContent, long reply_id, JsonHttpResponseHandler handler) {
		// Get the endpoint to send the request to
		String endpoint = getApiUrl("statuses/update.json");

		// Make a request to send the tweet
		RequestParams params = new RequestParams();
		params.put("status", tweetContent);

		// If the mode is reply, reply to the given reply id (which won't be -1 if
		// reply mode is on)
		if (reply_id != -1) {
			params.put("in_reply_to_status_id", reply_id);
		}

		client.post(endpoint, params, "", handler);
	}

	// Given a tweet id, send a request to like this tweet
	public void likeTweet(long id, JsonHttpResponseHandler handler) {
		// Get the endpoint to send the request to
		String endpoint = getApiUrl("favorites/create.json");

		// Make a request to like the tweet
		RequestParams params = new RequestParams();
		params.put("id", id);
		client.post(endpoint, params, "", handler);
	}

	// Given a tweet id, send a request to like this tweet
	public void unlikeTweet(long id, JsonHttpResponseHandler handler) {
		// Get the endpoint to send the request to
		String endpoint = getApiUrl("favorites/destroy.json");

		// Make a request to unlike the tweet
		RequestParams params = new RequestParams();
		params.put("id", id);
		client.post(endpoint, params, "", handler);
	}

	// Given a tweet id, send a request to retweet this tweet
	public void retweetTweet(long id, JsonHttpResponseHandler handler) {
		// Get the endpoint to send the request to
		String endpoint = getApiUrl("statuses/retweet.json");

		// Make a request to retweet the tweet
		RequestParams params = new RequestParams();
		params.put("id", id);
		client.post(endpoint, params, "", handler);
	}

	// Given a tweet id, send a request to retweet this tweet
	public void unretweetTweet(long id, JsonHttpResponseHandler handler) {
		// Get the endpoint to send the request to
		String endpoint = getApiUrl("statuses/unretweet.json");

		// Make a request to unretweet the tweet
		RequestParams params = new RequestParams();
		params.put("id", id);
		client.post(endpoint, params, "", handler);
	}

	// Send a request to get information on a given tweet
	public void getTweetInfo(String tweetId, JsonHttpResponseHandler handler) {
		// Get the endpoint to send the request to
		String endpoint = getApiUrl("statuses/lookup.json");

		// Make a request to get the tweet
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		params.put("tweet_mode", "extended");
		client.post(endpoint, params, "", handler);
	}

	// Get the followers of a user with a given id
	public void getFollowers(long id, JsonHttpResponseHandler handler) {
		// Get the endpoint to send the request to
		String endpoint = getApiUrl("followers/list.json");

		// Make a request to get the followers
		RequestParams params = new RequestParams();
		params.put("id", id);
		params.put("count", 15);
		client.get(endpoint, params, handler);
	}
}

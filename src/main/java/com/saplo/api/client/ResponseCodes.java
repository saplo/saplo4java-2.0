/**
 * 
 */
package com.saplo.api.client;

/**
 * @author progre55
 *
 */
public class ResponseCodes {

	/*
	 * CLIENT SIDE ERROR CODES
	 */
	public static final int CODE_ERR_OTHER_EXCEPTION = 800;

	public static final int CODE_UNKNOWN_EXCEPTION = 899;
	public static final String MSG_UNKNOWN_EXCEPTION = "Unknown exception occurred";

	public static final int CODE_SOCKET_EXCEPTION = 898;
	public static final String MSG_SOCKET_EXCEPTION = "Socket exception occurred";

	public static final int CODE_API_DOWN_EXCEPTION = 897;
	public static final String MSG_API_DOWN_EXCEPTION = "The API returned with status code %s";

	public static final int CODE_RECONNECTED = 896;
	public static final String MSG_RECONNECTED = "Reconnected to the API, re-try your operation";
	
	public static final int CODE_MALFORMED_RESPONSE = 895;
	public static final String MSG_MALFORMED_RESPONSE = "Received malformed response from the API";
		
	public static final int CODE_JSON_EXCEPTION = 894;
	public static final String MSG_JSON_EXCEPTION = "JSONException";

	/*
	 * GENERAL RPC ERROR CODES
	 */

	public final static int CODE_ERR_NOSESSION = 595;
	public final static String MSG_ERR_NOSESSION = "the session/access_token has expired";

	public static final int CODE_TOKEN_EXPIRED = 1000;
	public static final String MSG_TOKEN_EXPIRED = "Token has expired.";

	public static final int CODE_MISSING_REQ_FIELD = 1001;
	public static final String MSG_MISSING_REQ_FIELD = "Missing required field(s).";

	public static final int CODE_LANG_UNSUPPORT = 1002;
	public static final String MSG_LANG_UNSUPPORT = "This language is not supported yet.";

	public static final int CODE_PROCESSING = 1003;
	public static final String MSG_PROCESSING = "Processing request.";

	public static final int CODE_STILL_PROCESSING = 1004;
	public static final String MSG_STILL_PROCESSING = "Sorry, still processing but I am working as fast as I can.";

	public static final int CODE_MAX_WAIT = 1005;
	public static final String MSG_MAX_WAIT = "Sorry, your max waiting time was reached, but I am still processing.";

	public static final int CODE_NO_RESULTS = 1006;
	public static final String MSG_NO_RESULTS = "No results were found.";

	public static final int CODE_NO_API_CALLS = 1007;
	public static final String MSG_NO_API_CALLS = "Sorry, you do not have any API Calls left.";

	public static final int CODE_API_LIMIT_REACHED = 1008;
	public static final String MSG_API_LIMIT_REACHED = "Maximum API Calls for this hour reached.";

	public static final int CODE_API_PERMISSION_FAIL = 1009;
	public static final String MSG_API_PERMISSION_FAIL = "You do not have permission to use the API.";

	public static final int CODE_CALC_FAILURE = 1010;
	public static final String MSG_CALC_FAILURE = "An unknown error occurred while completing your task.";

	public static final int CODE_CALC_PAYMENT = 1011;
	public static final String MSG_CALC_PAYMENT = "Payment Required (betala, snälla!)";

	/*
	 * AUTH RPC ERR CODES
	 */

	public static final int CODE_LOGIN_INCORRECT = 1101;
	public static final String MSG_LOGIN_INCORRECT = "API-key and/or Secret-key incorrect";

	/*
	 * CORPUS RPC ERROR CODES
	 */

	public static final int CODE_CORPUS_NO_PERMISSION = 1201;
	public static final String MSG_CORPUS_NO_PERMISSION = "No permission for that operation on corpus %s.";

	public static final int CODE_CORPUS_NO_EXISTS = 1202;
	public static final String MSG_CORPUS_NO_EXISTS = "No corpus with id %s exists for user, need to create a corpus first.";

	public static final int CODE_MAX_CORPUS = 1203;
	public static final String MSG_MAX_CORPUS = "Maximum number of corpuses exceeded.";

	public static final int CODE_CORPUS_NO_ARTICLE = 1204;
	public static final String MSG_CORPUS_NO_ARTICLE = "No such article exists.";
	public static final String MSG_CORPUS_NO_ARTICLE_PARAMS = "No article exists with id %s in corpus %s.";

	public static final int CODE_CORPUS_DUPLICATE_ARTICLE = 1205;
	public static final String MSG_CORPUS_DUPLICATE_ARTICLE = "Article %s already exists.";

	public static final int CODE_CORPUS_ARTICLE_LANG_MISSMATCH = 1206;
	public static final String MSG_CORPUS_ARTICLE_LANG_MISSMATCH = "Article language (%s) and collection language (%s) don't match.";

	public static final int CODE_CORPUS_DONT_EXIST = 1207;
	public static final String MSG_CORPUS_DONT_EXIST = "No corpus with id %s.";

	public static final int CODE_CORPUS_WRONG_DATE_FORMAT = 1208;
	public static final String MSG_CORPUS_WRONG_DATE_FORMAT = "Wrong date format. Should be 'YYYY-MM-DD HH:MM:SS'";

	public static final int CODE_CORPUS_NO_TEXT = 1209;
	public static final String MSG_CORPUS_NO_TEXT = "You need to provide body text.";

	public static final int CODE_CORPUS_NO_NAME = 1210;
	public static final String MSG_CORPUS_NO_NAME = "A corpus name is required.";

	public static final int CODE_ARTICLE_HEADLINE_LONG = 1211;
	public static final String MSG_ARTICLE_HEADLINE_LONG = "Article headline too long, max length is %s chars. Your article headline is %s chars long.";

	public static final int CODE_ARTICLE_AUTHORS_LONG = 1212;
	public static final String MSG_ARTICLE_AUTHORS_LONG = "Article authors too long, max length is %s chars. Your article authors is %s chars long.";

	public static final int CODE_ARTICLE_URL_LONG = 1213;
	public static final String MSG_ARTICLE_URL_LONG = "Article URL too long, max length is %s chars. Your article url is %s chars long.";

	public static final int CODE_ARTICLE_BODY_LONG = 1214;
	public static final String MSG_ARTICLE_BODY_LONG = "Article body too long, max length is %s chars. Your article body is %s chars long.";

	public static final int CODE_ARTICLE_LEAD_LONG = 1215;
	public static final String MSG_ARTICLE_LEAD_LONG = "Article lead too long, max length is %s chars. Your article lead is %s chars long.";

	public static final int CODE_ARTICLE_INVALID_CONTENT = 1216;
	public static final String MSG_ARTICLE_INVALID_CONTENT = "Malformed HTML detected.";

	/*
	 * TAGS RPC ERROR CODES
	 */

	public static final int CODE_TAGS_NO_RESULTS = 1301;
	public static final String MSG_TAGS_NO_RESULTS = "Could not find any tags for article %s.";

	public static final int CODE_TAGS_NO_TAG_ID = 1302;
	public static final String MSG_TAGS_NO_TAG_ID = "Could not find any tags with id %s.";

	public static final int CODE_TAGS_NO_BLACKLIST = 1303;
	public static final String MSG_TAGS_NO_BLACKLIST = "No blacklist exists.";

	public static final int CODE_TAGS_BLACKWORD_EXISTS = 1304;
	public static final String MSG_TAGS_BLACKWORD_EXISTS = "Blackword already exists.";

	public static final int CODE_TAGS_NO_TAGWORD_PROVIDED = 1305;
	public static final String MSG_TAGS_NO_TAGWORD_PROVIDED = "No tag word provided.";

	public static final int CODE_TAGS_NO_SUCH_TAGTYPEID = 1306;
	public static final String MSG_TAGS_NO_SUCH_TAGTYPEID = "No tag type %s exists.";

	/*
	 * CONTEXT RPC ERROR CODES
	 */

	public static final int CODE_CONTEXT_NO_CONTEXT_EXISTS = 1401;
	public static final String MSG_CONTEXT_NO_CONTEXT_EXISTS = "No context with id %s.";

	public static final int CODE_CONTEXT_NO_PERMISSION = 1402;
	public static final String MSG_CONTEXT_NO_PERMISSION = "No permission for that operation on context %s.";

	public static final int CODE_CONTEXT_NO_NAME = 1403;
	public static final String MSG_CONTEXT_NO_NAME = "No context name provided.";

	public static final int CODE_CONTEXT_MAX_LIMIT = 1404;
	public static final String MSG_CONTEXT_MAX_LIMIT = "Maximum limit of contexts reached.";

	public static final int CODE_CONTEXT_NAME_EXISTS = 1405;
	public static final String MSG_CONTEXT_NAME_EXISTS = "Context with provided name already exists.";

	public static final int CODE_CONTEXT_LIST_EMPTY = 1406;
	public static final String MSG_CONTEXT_LIST_EMPTY = "Context list empty.";

	public static final int CODE_CONTEXT_EMPTY = 1407;
	public static final String MSG_CONTEXT_EMPTY = "Context %s is empty. Please add articles first.";

	/*
	 * MATCH RPC ERROR CODES
	 */

	public static final int CODE_MATCH_NO_RESULTS = 1501;
	public static final String MSG_MATCH_NO_RESULTS = "No matching results were found.";

	public static final int CODE_MATCH_NO_MATCH_ID = 1502;
	public static final String MSG_MATCH_NO_MATCH_ID = "Could not find any match with id %s.";

	public static final int CODE_MATCH_MIN_THRESHOLD = 1503;
	public static final String MSG_MATCH_MIN_THRESHOLD = "Minimum threshold must be less than 1.";

	public static final int CODE_MATCH_MAX_THRESHOLD = 1503;
	public static final String MSG_MATCH_MAX_THRESHOLD = "Maximum threshold must be higher than 0.";

	/*
	 * SAPLO CLIENT ERROR CODES
	 */
	public static final int CODE_CLIENT_FIELD = 1001;
	public static final String MSG_CLIENT_FIELD = "Missing required field(s): %s";

}

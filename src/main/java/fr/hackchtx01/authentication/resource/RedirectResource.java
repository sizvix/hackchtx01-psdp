package fr.hackchtx01.authentication.resource;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

/**
 * Used only for testing OAuth2 on localhost
 * @author yoan
 */
@Path("/public/redirect")
public class RedirectResource {
	@Context
    HttpHeaders httpHeaders;
	@Context
    UriInfo uriInfo;
    
	@GET
    public String redirect() {
        JSONObject object = new JSONObject();
        JSONObject headers = new JSONObject(); 
        JSONObject qp = new JSONObject();
        String json = "error!";
        try {
            for (Map.Entry<String, List<String>> entry : httpHeaders.getRequestHeaders().entrySet()) {
                headers.put(entry.getKey(), entry.getValue().get(0));
            }
            object.put("headers", headers);
            for (Map.Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
                qp.put(entry.getKey(), entry.getValue().get(0));
            }
            object.put("queryParameters", qp);
            json = object.toString(4);
        } catch (JSONException ex) {
            LoggerFactory.getLogger(RedirectResource.class).error(null, ex);
        }
        return json;
    }
}
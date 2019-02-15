package server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.util.Base64;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * Created by u016272 on 12/01/2017.
 */
public class token implements Serializable {
  String token=null;
  Long dtExpire=7*24*3600 * 1000 + System.currentTimeMillis();
  String refresh_token=null;
  String idUser="";
  String clientid="";
  String description="";
  String endpoint="";
  String secret="";

  public token(String token, Long dtExpire, String refresh_token, String endpoint, String secret_key, String clientid) {
    this.token = token;
    this.dtExpire = dtExpire;
    this.refresh_token=refresh_token;
    this.endpoint=endpoint;
    this.secret=secret_key;
    this.clientid=clientid;
  }

  public token(String res, String endpoint, String secret_key, String clientid) throws IOException {
    JsonNode node=Tools.toJSON(res);
    this.clientid=clientid;
    if(node.has("access_token"))this.token = node.get("access_token").asText();
    if(node.has("refresh_token"))this.refresh_token= node.get("refresh_token").asText();
    this.secret=secret_key;

    Long delay=3600L;
    if(node.has("expires_in"))delay=node.get("expires_in").asLong();
    if(node.has("expires"))delay=node.get("expires_in").asLong();
    this.dtExpire=System.currentTimeMillis()+delay*1000;

    this.endpoint=endpoint;
  }

  public token() {
  }
  //see https://developer.spotify.com/web-api/authorization-guide/#authorization-code-flow
  public Boolean refreshToken(String refreshPoint){
    if(refreshPoint==null)refreshPoint=this.getEndpoint();
    if(refresh_token!=null){
      String toPost="refresh_token="+this.refresh_token+"&grant_type=refresh_token";
      String authorization="Basic "+ Base64.encodeBase64String((this.getClientid()+":"+this.getSecret()).getBytes(Charset.forName("utf-8")));
      JsonNode jsonNode= null;
      try {
        jsonNode = Tools.toJSON(Tools.rest(refreshPoint,toPost,authorization,"application/x-www-form-urlencoded","post"));
      } catch (RestAPIException e) {
        e.printStackTrace();
      }
      if(jsonNode!=null){
        this.setToken(jsonNode.get("access_token").asText());
        this.setDtExpire(System.currentTimeMillis()+jsonNode.get("expires_in").asInt()*1000);
        return true;
      }
    }
    return false;
  }

  public boolean isValid(){
    if(this.getToken()==null)return false;
    long delay=this.getDelayExpire();
    return delay>0;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public String getIdUser() {
    return idUser;
  }

  public void setIdUser(String idUser) {
    this.idUser = idUser;
  }

  public String toString() {
    return this.getToken()+" - expire:"+Tools.getDate(this.getDtExpire());
  }

  public String getRefresh_token() {
    return refresh_token;
  }

  public void setRefresh_token(String refresh_token) {
    this.refresh_token = refresh_token;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getClientid() {
    return clientid;
  }

  public void setClientid(String clientid) {
    this.clientid = clientid;
  }

  public Long getDtExpire() {
    return dtExpire;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

	/**
     * Regle la date d'expiration du token
     * @param dtExpire en secondes
     */
  public void setDtExpire(Long dtExpire) {
    this.dtExpire = dtExpire;
  }

  public JsonNode call(String method, String args) throws RestAPIException {
    String s="";
    if(args!=null)s="&"+args;
    String url=this.getEndpoint()+"/"+method+"?access_token="+this.getToken()+s;
    String res=Tools.rest(url,null,null,null,"get");
    return Tools.toJSON(res);
  }

  public JsonNode call(String method, String tokenFieldName,String args) throws RestAPIException {
    String s="";
    if(args!=null)s="&"+args;
    return Tools.toJSON(Tools.rest(this.getEndpoint()+"/"+method+"?"+tokenFieldName+"="+this.getToken()+s,null,null,null,"get"));
  }

  public JsonNode callPost(String method, String args) throws RestAPIException {
    String s="";
    if(args!=null)s="&"+args;
    return Tools.toJSON(Tools.rest(this.getEndpoint()+"/"+method,"access_token="+this.getToken()+s,null,null,"post"));
  }


  public JsonNode call(String s) throws RestAPIException {
    return this.call(s,"format=json");
  }


	/**
   * Delai d'expiration du token
   * @return delai en secondes (1H si on n'avait pas de délai)
   */
  @JsonIgnore
  public Long getDelayExpire() {
    long delay = (this.getDtExpire() - System.currentTimeMillis()) / 1000;
    if(delay<=0)delay=3600;
    return delay;
  }
}

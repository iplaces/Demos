package utils

import java.io.{File, FileInputStream}

import com.google.inject.{Inject, Singleton}
import org.asynchttpclient.request.body.multipart._
import org.asynchttpclient.{AsyncHttpClient, RequestBuilder}
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsValue, _}
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.xml.Elem

/**
  * Created by ZYQ on 2016/3/10.
  **/
@Singleton
class HttpUtil @Inject()(ws: WSClient) {

  val log = Logger(this.getClass)

  def postJsonRequestSend(
                           methodName: String,
                           url: String,
                           parameters: List[(String, String)],
                           postData: JsValue) = {
    log.info("Post Request [" + methodName + "] Processing...")
    log.debug(methodName + " url=" + url)
    log.debug(methodName + " parameters=" + parameters)
    log.debug(methodName + " postData=" + postData.toString)
    val futureResult = ws.
      url(url).
      withFollowRedirects(follow = true).
      withRequestTimeout(10000.millis).
      withQueryString(parameters: _*).
      post(postData).map { response =>
      log.debug("postJsonRequestSend response headers:" + response.allHeaders)
      log.debug("postJsonRequestSend response body:" + response.body)
      if (response.status != 200) {
        val msg = s"postJsonRequestSend http failed url = $url, status = ${response.status}, text = ${response.statusText}, body = ${response.body.substring(0, 1024)}"
        log.warn(msg)
      }
      response.json
    }
    futureResult.onFailure {
      case e: Exception =>
        log.error(methodName + " error:" + e.getMessage, e)
        throw e
    }
    futureResult
  }

  def postFormRequestSend(
                           methodName: String,
                           url: String,
                           parameters: List[(String, String)],
                           form: Map[String, Seq[String]]
                         ) = {
    log.info("Post Request [" + methodName + "] Processing...")
    log.debug(methodName + " url=" + url)
    log.debug(methodName + " parameters=" + parameters)
    log.debug(methodName + " postData=" + form.toString)
    val futureResult = ws.
      url(url).
      withFollowRedirects(follow = true).
      withRequestTimeout(10000.millis).
      withQueryString(parameters: _*).
      post(form).map { response =>
      log.debug("postFormRequestSend response headers:" + response.allHeaders)
      log.debug("postFormRequestSend response body:" + response.body)
      if (response.status != 200) {
        val msg = s"postFormRequestSend http failed url = $url, status = ${response.status}, text = ${response.statusText}, body = ${response.body.substring(0, 1024)}"
        log.warn(msg)
      }
      response.body
    }
    futureResult.onFailure {
      case e: Exception =>
        log.error(methodName + " error:" + e.getMessage, e)
        throw e
    }
    futureResult
  }

  def postXmlRequestSend(
                          methodName: String,
                          url: String,
                          parameters: List[(String, String)],
                          postData: Elem) = {
    log.info("Post Request [" + methodName + "] Processing...")
    log.debug(methodName + " url=" + url)
    log.debug(methodName + " parameters=" + parameters)
    log.debug(methodName + " postData=" + postData.toString)
    val futureResult = ws.
      url(url).
      withFollowRedirects(follow = true).
      withRequestTimeout(10000.millis).
      withQueryString(parameters: _*).
      post(postData).map { response =>
      log.debug("postXmlRequestSend response headers:" + response.allHeaders)
      log.debug("postXmlRequestSend response body:" + response.body)
      if (response.status != 200) {
        val msg = s"postJsonRequestSend http failed url = $url, status = ${response.status}, text = ${response.statusText}, body = ${response.body.substring(0, 1024)}"
        log.warn(msg)
      }
      response.xml
    }
    futureResult.onFailure {
      case e: Exception =>
        log.error(methodName + " error:" + e.getMessage, e)
        throw e
    }
    futureResult
  }


  def getRequestSend(
                      methodName: String,
                      url: String,
                      parameters: List[(String, String)]) = {
    log.info("Get Request [" + methodName + "] Processing...")
    log.debug(methodName + " url=" + url)
    log.debug(methodName + " parameters=" + parameters)
    val futureResult = ws.
      url(url).
      withFollowRedirects(follow = true).
      withRequestTimeout(10000.millis).
      withQueryString(parameters: _*).
      get().map { response =>
      log.debug("getRequestSend response headers:" + response.allHeaders)
      log.debug("getRequestSend response body:" + response.body)
      if (response.status != 200) {
        val msg = s"getRequestSend http failed url = $url, status = ${response.status}, text = ${response.statusText}, body = ${response.body.substring(0, 24)}"
        log.warn(msg)
      }
      response.json
    }
    futureResult.onFailure {
      case e: Exception =>
        log.error(methodName + " error:" + e.getMessage, e)
        throw e
    }
    futureResult
  }


  def getJsonRequestSend(
                          methodName: String,
                          url: String,
                          parameters: List[(String, String)]) = {
    log.info("Get Request [" + methodName + "] Processing...")
    log.debug(methodName + " url=" + url)
    log.debug(methodName + " parameters=" + parameters)
    val futureResult = ws.
      url(url).
      withFollowRedirects(follow = true).
      withRequestTimeout(Duration(10, scala.concurrent.duration.SECONDS)).
      withQueryString(parameters: _*).
      get().map { response =>
      log.debug("getRequestSend response headers:" + response.allHeaders)
      log.debug("getRequestSend response body:" + response.body)
      if (response.status != 200) {
        val body = if (response.body.length > 1024) response.body.substring(0, 1024) else response.body
        val msg = s"getRequestSend http failed url = $url, status = ${response.status}, text = ${response.statusText}, body = ${body}"
        log.warn(msg)
      }

      response.json

    }

    futureResult.onFailure {
      case e: Exception =>
        log.error(methodName + " error:" + e.getMessage, e)
        throw e
    }
    futureResult
  }


  private def readToBytes(stream: java.io.InputStream): Array[Byte] = {
    val len = stream.available
    val bytes = new Array[Byte](len)

    var cur = 0
    var finished = false
    while (!finished && cur != len) {
      val read = stream.read(bytes, cur, len - cur)
      finished = read == -1
      if (!finished)
        cur += read
    }
    assert(cur == len)
    stream.close()
    bytes
  }

  def postFileRequestSend(
                           methodName: String,
                           url: String,
                           parameters: List[(String, String)],
                           file: File,
                           fileName: String,
                           partKey: String): Future[JsValue] = {
    log.info("Post File Request [" + methodName + "] Processing...")
    log.debug(methodName + " url=" + url)
    log.debug(methodName + " parameters=" + parameters)
    log.debug(methodName + " file=" + file.getName)

    val part = new ByteArrayPart(partKey,
      readToBytes(new FileInputStream(file)),
      "text/plain",
      java.nio.charset.Charset.forName("UTF-8"),
      fileName
    )

    val client = ws.underlying[AsyncHttpClient]

    val requestBuilder = new RequestBuilder("POST")
    requestBuilder.setUrl(url)
    parameters.foreach(kv => requestBuilder.addQueryParam(kv._1, kv._2))
    requestBuilder.setFollowRedirect(true)
    requestBuilder.addBodyPart(part)

    val result = Future {
      client.prepareRequest(requestBuilder).execute().get
    }

    result.map { response =>
      log.debug(s"response status: ${response.getStatusCode}")
      log.debug(s"response headers: ${response.getHeaders}")
      if (response.getStatusCode != 200) {
        val msg = s"postMimeFileRequestSend http failed url = $url, status = ${response.getStatusCode}, text = ${response.getStatusText}, body = ${response.getResponseBody}"
        log.warn(msg)
      }

      Json.parse(response.getResponseBody)
    }

  }

}
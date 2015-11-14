package snatcher.face.com.facesnatcher;

import com.kii.cloud.storage.Kii.Site;

public class AppConstants {
    public static final String APP_ID = "f860c861";
    public static final String APP_KEY = "7879d4d49a6b4af1065f3dca1cd37f09";
    public static final Site APP_SITE = Site.JP;
    public static final String APP_BUCKET_NAME = "tutorial";
    public static final String ACCESS_TOKEN = "PAzSqUSRZuQPCF8KwjYUKExzU6EcpfUNno9iAUvOr2o";
}

// $ curl -v -X POST -H "X-Kii-AppID: f860c861" -H "X-Kii-AppKey:7879d4d49a6b4af1065f3dca1cd37f09 "  -H "Content-Type: application/json"   "https://api-jp.kii.com/api/oauth2/token" -d '{"username": "aaaa", "password": "aaaa"}'


/*

➜  FaceSnatcher git:(master) ✗ curl -v -X POST -H "X-Kii-AppID: f860c861" -H "X-Kii-AppKey:7879d4d49a6b4af1065f3dca1cd37f09 "  -H "Content-Type: application/json"   "https://api-jp.kii.com/api/oauth2/token" -d '{"username": "aaaa", "password": "aaaa"}'

*   Trying 54.238.63.161...
* Connected to api-jp.kii.com (54.238.63.161) port 443 (#0)
* TLS 1.2 connection using TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256
* Server certificate: *.kii.com
* Server certificate: Go Daddy Secure Certificate Authority - G2
* Server certificate: Go Daddy Root Certificate Authority - G2
> POST /api/oauth2/token HTTP/1.1
> Host: api-jp.kii.com
> User-Agent: curl/7.43.0
> Accept: *
        > X-Kii-AppID: f860c861
        > X-Kii-AppKey:7879d4d49a6b4af1065f3dca1cd37f09
        > Content-Type: application/json
        > Content-Length: 40
        >
        * upload completely sent off: 40 out of 40 bytes
< HTTP/1.1 200 OK
< Accept-Ranges: bytes
< Access-Control-Allow-Origin: *
< Access-Control-Expose-Headers: Content-Type, Authorization, Content-Length, X-Requested-With, ETag, X-Step-Count
< Age: 0
< Cache-Control: max-age=0, no-cache, no-store
< Content-Type: application/json;charset=UTF-8
< Date: Sat, 14 Nov 2015 08:58:08 GMT
< Server: nginx/1.2.3
< X-HTTP-Status-Code: 200
< Content-Length: 173
< Connection: keep-alive
<
{
        "id" : "d3d808a00022-dbcb-5e11-8aa8-09aef89e",
        "access_token" : "PAzSqUSRZuQPCF8KwjYUKExzU6EcpfUNno9iAUvOr2o",
        "expires_in" : 2147483646,
        "token_type" : "Bearer"
        * Connection #0 to host api-jp.kii.com left intact
        }%

*/

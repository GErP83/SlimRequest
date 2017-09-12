# SlimRequest

SlimRequest is tiny library for android network handling. Not use any 3rd party library, it has zero dependencies, has no other layers, not working of top of some other library. Only the top of HttpURLConnection. It has a 28 kB size and has 160 methods only.

### What it can do
- can call basic HTTP/HTTPS request: GET, POST, DELETE, PUT, PATCH
- can check internet availability, returns NETWORK error, you don't need to check it yourself
- you don't have to worry about main thread using
- can set content type like "application/json; charset=utf-8"
- can add params for the requests with key/value pair, like addParam("name", "Josh")
- can add header for the requests with key/value pair, same as param
- can add a JSONObject for the request body
- can set username/password with Authenticator
- can say every https is trusted, can be risky ;)
- can load a SSL cert from assets for https calls
- can set return type, types: STRING, JSON_OBJECT, JSON_ARRAY, BYTES, FILE
- can set connection timeout for the request
- can set read timeout for the request
- can set chunk size, for upload/download
- can add a file to upload with multipart entity, has progress handling as well
- can add a target file to download to, has progress handling as well
- can handle a session: save, add, clear
- can add multiple request to a stack, the stack runs all request async and returns when all is done, with all response
- can add multiple request to a chain, can create Transfers between the requests, with a Transfer you can call the next request with the previous response
- can cancel a request, but can cancel a stack or a chain too
- always returns one type of result for request success or request fail, the results has info about:
  - response code
  - data, that you need
  - error type
  - error msg
  - all response headers, if needed
  - run time im milliseconds
  - bytes uploaded
  - bytes downloaded
  - skipped (will explain with stacks)
  
  ### Requirements:
  Android api 2.3+ and permissions:
  
  ```
  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  ```
  
  ### Lisence: WTFPL
  
  

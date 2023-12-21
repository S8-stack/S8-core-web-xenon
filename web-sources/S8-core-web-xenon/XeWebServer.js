import { S8WebServer } from "/S8-api/S8WebServer.js";

/**
 * 
 */
export class XeWebServer extends S8WebServer {


    /**
     * @type{boolean}
     */
    DEBUG_isVerbose = true;


    /**
     * @type {string} retrieve origin
     */
    origin;


    constructor() {
        super();
        this.origin = window.location.origin;
    }



    /**
     * 
     * @param {*} method 
     * @param {*} capacity 
     * @returns a { S8Request }
     */
    createRequest_POST(method, capacity) {
        throw "Import for " + method + ", " + capacity + ", " + callback + " failed. Must provide implementation.";
    }


    /**
     * 
     * @param {string} requestPath 
     * @param {string} responseType 
     * @param {Function} responseCallback 
     */
    sendRequest_HTTP2_GET(requestPath, responseType, responseCallback) {

        /**
                * Relies on browser cache for speeding things up
                */
        let xhr = new XMLHttpRequest();

        // first line
        xhr.open("GET", this.origin + requestPath, true);
        xhr.responseType = responseType;

        // headers
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.setRequestHeader('Access-Control-Allow-Origin', "*");
        xhr.setRequestHeader('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS');
        xhr.setRequestHeader('Access-Control-Allow-Headers', 'Cookie, Content-Type, Authorization, Content-Length, X-Requested-With');
        xhr.setRequestHeader('Access-Control-Expose-Headers', 'Set-Cookie, X-Powered-By');

        let _this = this;
        // Hook the event that gets called as the request progresses
        xhr.onreadystatechange = function () {
            // If the request is "DONE" (completed or failed)
            if (xhr.readyState == 4) {
                // If we got HTTP status 200 (OK)
                if (xhr.status == 200) {
                    responseCallback(xhr.responseText);
                }
            }
        };

        // fire request
        xhr.send(null);
    }


    /**
     * 
     * @param {*} requestArrayBuffer 
     * @param {*} responseCallback 
     */
    sendRequest_HTTP2_POST(requestArrayBuffer, responseCallback) {

        // create request
        let request = new XMLHttpRequest();

        // ask for array buffer type reponse
        request.responseType = "arraybuffer";

        // setup XMLHttpRequest.open(method, url, async)
        request.open("POST", this.origin, true);

        // callback
        request.onreadystatechange = function () {
            if (this.readyState == 4 && this.status == 200) {

                let responseArrayBuffer = request.response; // Note: not oReq.responseText
                if (responseArrayBuffer) {
                    responseCallback(responseArrayBuffer);
                    if (this.DEBUG_isVerbose) {
                        console.log("[Helium/system] successfully loaded response");
                    }
                }
                else {
                    console.log("[Helium/system] No response array buffer");
                }
            }
        };

        // fire
        request.send(new Uint8Array(requestArrayBuffer));


        /*
        let requestBody = new Uint8Array(requestArrayBuffer);
        fetch(this.origin, {
            method: 'POST',
            body: requestBody
        })
        .then((response) => {
            if (!response.ok) {
                throw new Error(`HTTP error, status = ${response.status}`);
            }
            return response.arrayBuffer();
        })
        .then((arraybuffer) => { 
            responseCallback(arraybuffer);
        })
        .catch((error) => console.log("[Helium/system] No response array buffer: due to "+error));
        */

    }

}




import { InboardScreen } from '/s8-web-front/inboard/InboardScreen.js';

import { ByteOutflow } from '/s8-io-bytes/ByteOutflow.js';
import { ByteInflow } from '/s8-io-bytes/ByteInflow.js';

import { S8 } from '/s8-io-bohr-atom/S8.js';
import { NeBranch } from '/s8-io-bohr-neon/NeBranch.js';

import { XENON_RequestKeywords, XENON_ResponseKeywords } from '/s8-web-xenon/XeProtocol.js';




export const launch = function(){
    const launcher = new XeLauncher();
    launcher.start();
}


class XeLauncher {


    /**
     * @type{InboardScreen}
     */
    inboardScreen;

    constructor() {
    }


    start() {
        const _this = this;
        this.inboardScreen = new InboardScreen("AlphaVentor", function (username, password) {
            //console.log(username);
            //console.log(password);
            _this.login(username, password);
        });
        this.inboardScreen.start();
        document.body.appendChild(this.inboardScreen.getEnvelope());
    }




    login(username, password) {


        let requestArrayBuffer = new ArrayBuffer(256);
        let outflow = new ByteOutflow(requestArrayBuffer);
        outflow.putUInt8(XENON_RequestKeywords.LOG_IN);
        outflow.putStringUTF8(username);
        outflow.putStringUTF8(password);

        const _this = this;
        S8.sendRequest_HTTP2_POST(requestArrayBuffer, function (responseArrayBuffer) {
            let inflow = new ByteInflow(responseArrayBuffer);
            let code = inflow.getUInt8();
            switch (code) {

                case XENON_ResponseKeywords.SUCCESSFULLY_LOGGED_IN:
                    _this.boot();
                    break;

                case XENON_ResponseKeywords.LOG_IN_FAILED:
                    alert("login-failed");
                    break;

            }
        });
    }



    boot() {

        let requestArrayBuffer = new ArrayBuffer(64);
        let outflow = new ByteOutflow(requestArrayBuffer);
        outflow.putUInt8(XENON_RequestKeywords.BOOT);

        const _this = this;
        S8.sendRequest_HTTP2_POST(requestArrayBuffer, function (responseArrayBuffer) {

            /* clear screen */
            _this.clearScreen();

            /* prepare screen node */
            const screenNode = document.createElement("div");
            document.body.appendChild(screenNode);

            /* Equip S8 with a NEON Branch, holding screen node */
            S8.branch = new NeBranch(screenNode, XENON_RequestKeywords.RUN_FUNC);

            /* run branch */
            let inflow = new ByteInflow(responseArrayBuffer);
            S8.branch.consume(inflow);
        });
    }




    clearScreen() {
        this.inboardScreen.stop();
        while (document.body.firstChild != undefined) {
            document.body.removeChild(document.body.firstChild);
        }
    }

}











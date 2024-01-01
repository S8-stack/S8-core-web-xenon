


import { S8 } from '/S8-api/S8Context.js';

import { ByteOutflow } from '/S8-core-io-bytes/ByteOutflow.js';
import { ByteInflow } from '/S8-core-io-bytes/ByteInflow.js';

import { NeBranch } from '/S8-core-bohr-neon/NeBranch.js';

import { XENON_Keywords } from '/S8-core-web-xenon/XeProtocol.js';


import { XeWebServer } from './XeWebServer.js';
import { XeWebPage } from './XeWebPage.js';


export const launch = function(){

    /* define S8 context */
    S8.server = new XeWebServer();
    S8.page = new XeWebPage();

    /* create launcher */
    const launcher = new XeLauncher();

    /* launch! */
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

        let requestArrayBuffer = new ArrayBuffer(64);
        let outflow = new ByteOutflow(requestArrayBuffer);
        outflow.putUInt8(XENON_Keywords.BOOT);

        const _this = this;
        S8.server.sendRequest_HTTP2_POST(new Uint8Array(requestArrayBuffer), function (responseArrayBuffer) {

            /* clear screen */
            _this.clearScreen();

            /* prepare screen node */
            const screenNode = document.createElement("div");
            document.body.appendChild(screenNode);

            /* Equip S8 with a NEON Branch, holding screen node */
            S8.branch = new NeBranch(screenNode, XENON_Keywords.RUN_FUNC);

            /* run branch */
            let inflow = new ByteInflow(responseArrayBuffer);
            
            /* consume inflow */
            S8.branch.consume(inflow);
        });
    }


    clearScreen() {
        while (document.body.firstChild != undefined) {
            document.body.removeChild(document.body.firstChild);
        }
    }
}











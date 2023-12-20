


import { ByteOutflow } from '/S8-core-io-bytes/ByteOutflow.js';
import { ByteInflow } from '/S8-core-io-bytes/ByteInflow.js';

import { S8 } from '/S8-core-bohr-atom/S8.js';
import { NeBranch } from '/S8-core-bohr-neon/NeBranch.js';

import { XENON_Keywords } from '/S8-core-web-xenon/XeProtocol.js';




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

        let requestArrayBuffer = new ArrayBuffer(64);
        let outflow = new ByteOutflow(requestArrayBuffer);
        outflow.putUInt8(XENON_Keywords.BOOT);

        const _this = this;
        S8.sendRequest_HTTP2_POST(requestArrayBuffer, function (responseArrayBuffer) {

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











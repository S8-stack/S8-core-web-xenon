import { S8 } from "/S8-api/S8Context.js";
import { S8Page } from "/S8-api/S8Page.js";




export class XePage extends S8Page {



	/**
	 * @type {Map<string, *>}
	 * // build stylesheets map
	 */
	map_CSS_stylesheets = new Map();


	/**
	 * @type {Map<string, *> }
	 */
	map_SVG_sources = new Map();



	/**
	 * @type{Object} 
	 * Previously active object: the last which sends a signal
	 */
	focus = null;

	/**
	 * @type{HTMLDivElement}
	 * Node for rendering of all base displayed elements
	 */
	baseLayerNode;


	/**
	 * @type{HTMLDivElement}
	 * Node for rendering of all popovers (one root node at a time)
	 */
	popoverLayerNode;


	constructor() {
		super();


		/* clear screen */
		while (document.body.firstChild != undefined) {
			document.body.removeChild(document.body.firstChild);
		}

		this.CSS_import("/S8-core-web-xenon/XePage.css");

		/* prepare base layer */
		this.baseLayerNode = document.createElement("div");
		this.baseLayerNode.className = "xenon-page-layer-base";
		document.body.appendChild(this.baseLayerNode);

		/* prepare popover layer */
		this.popoverLayerNode = document.createElement("div");
		this.popoverLayerNode.className = "xenon-page-layer-popover";
		document.body.appendChild(this.popoverLayerNode);

		/*
		const _this = this;
		document.body.addEventListener("scroll", function(){ _this.HTML_clearPopover(); }, false);
		*/
	}



	/**
	 * 
	 * @param {*} name 
	 */
	CSS_import(name) {
		if (!this.map_CSS_stylesheets.has(name)) {

			/** create a new link node and append it to the document head section */
			let linkNode = document.createElement("link");
			linkNode.setAttribute("type", "text/css");
			linkNode.setAttribute("rel", "stylesheet");
			linkNode.setAttribute("href", name);
			document.head.appendChild(linkNode);

			// document.head.innerHTML += `<link type="text/css" rel="stylesheet" href=${name}>`;
			this.map_CSS_stylesheets.set(name, true);
		}
	}





	/**
	 * 
	 * @param {HTMLElement} target 
	 * @param {number} code 
	 * @param {number} width 
	 * @param {number} height 
	 */
	SVG_insertByCode(target, code, width, height) {
		S8WebFront.SVG_insertByName(target, S8_FlatIcons_Map[code], width, height);
	}


	/**
	 * 
	 * @param {HTMLElement} target 
	 * @param {string} name 
	 * @param {number} width 
	 * @param {number} height 
	 */
	SVG_insertByPathname(target, pathname, width, height) {

		let svgSource0 = this.map_SVG_sources.get(pathname);

		let injector = function (source) {
			target.innerHTML = source;
			let svgNode = target.getElementsByTagName("svg")[0];
			svgNode.setAttribute("width", width);
			svgNode.setAttribute("height", height);
		}

		if (svgSource0 != undefined) {
			injector(svgSource0);
		}
		else {
			let _this = this;
			S8.server.sendRequest_HTTP2_GET(pathname, "text", function (svgSource1) {
				_this.map_SVG_sources.set(pathname, svgSource1);
				injector(svgSource1);
			});
		}
	}






	/**
	 * Efficiently remove children nodes of a node
	 * @param {*} node 
	 */
	removeChildren(node) {
		/* An earlier edit to this answer used firstChild, 
		but this is updated to use lastChild as in computer-science, 
		in general, it's significantly faster to remove the last 
		element of a collection than it is to remove the first element 
		(depending on how the collection is implemented). */
		while (node.firstChild) {
			node.removeChild(node.lastChild);
		}
	}



	/**
	 * @override {HTML_setRootElement}
	 * @param {HTMLElement} node 
	 */
	HTML_setRootElement(node) {
		/* An earlier edit to this answer used firstChild, 
		but this is updated to use lastChild as in computer-science, 
		in general, it's significantly faster to remove the last 
		element of a collection than it is to remove the first element 
		(depending on how the collection is implemented). */
		while (this.baseLayerNode.firstChild) {
			this.baseLayerNode.removeChild(this.baseLayerNode.lastChild);
		}
		this.baseLayerNode.appendChild(node);
	}

	/**
	 * @param {HTMLElement} node 
	 */
	HTML_setPopover(node) {
		this.HTML_clearPopover();
		this.popoverLayerNode.appendChild(node);
	}

	HTML_clearPopover() {
		while(this.popoverLayerNode.firstChild) {
			this.popoverLayerNode.removeChild(this.popoverLayerNode.lastChild);
		}
	}


	/**
	 * 
	 * @param {Object} object 
	 */
	setFocusOn(object) {
		/* unfocus */
		if (this.focus != null) {
			if (this.focus.S8_unfocus) { this.focus.S8_unfocus(); }
			else { console.log("Object missing a S8_unfocus method: " + previous); }
		}

		/* replace focus */
		this.focus = object;
	}


	/**
	 * 
	 */
	loseFocus() {
		/* unfocus */
		if (this.focus != null) {
			if (this.focus.S8_unfocus) { this.focus.S8_unfocus(); }
			else { console.log("Object missing a S8_unfocus method: " + previous); }
		}

		/* replace focus */
		this.focus = null;
	}
}
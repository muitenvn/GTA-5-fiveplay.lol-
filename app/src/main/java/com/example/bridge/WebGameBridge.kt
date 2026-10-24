package com.example.bridge

import android.webkit.WebView

object WebGameBridge {

    fun sendKey(webView: WebView?, key: String, code: String, keyCode: Int, isDown: Boolean) {
        if (webView == null) return
        val eventType = if (isDown) "keydown" else "keyup"
        val js = """
            (function() {
                try {
                    var opts = {
                        key: '$key',
                        code: '$code',
                        keyCode: $keyCode,
                        which: $keyCode,
                        bubbles: true,
                        cancelable: true
                    };
                    var evt = new KeyboardEvent('$eventType', opts);
                    document.dispatchEvent(evt);
                    window.dispatchEvent(evt);
                    var active = document.activeElement || document.body;
                    if (active) active.dispatchEvent(evt);
                    var targets = document.querySelectorAll('canvas, video, iframe, [tabindex]');
                    for (var i = 0; i < targets.length; i++) {
                        targets[i].dispatchEvent(evt);
                    }
                } catch(e) {}
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    fun sendMouseClick(webView: WebView?, button: Int, isDown: Boolean) {
        if (webView == null) return
        val eventType = if (isDown) "mousedown" else "mouseup"
        val js = """
            (function() {
                try {
                    var evt = new MouseEvent('$eventType', {
                        bubbles: true,
                        cancelable: true,
                        button: $button,
                        buttons: ${if (isDown) (if (button == 2) 2 else 1) else 0},
                        clientX: window.innerWidth / 2,
                        clientY: window.innerHeight / 2
                    });
                    document.dispatchEvent(evt);
                    var active = document.elementFromPoint(window.innerWidth / 2, window.innerHeight / 2) || document.body;
                    if (active) active.dispatchEvent(evt);
                    var targets = document.querySelectorAll('canvas, video, iframe');
                    for (var i = 0; i < targets.length; i++) {
                        targets[i].dispatchEvent(evt);
                    }
                } catch(e) {}
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    fun sendMouseMove(webView: WebView?, dx: Float, dy: Float) {
        if (webView == null) return
        val js = """
            (function() {
                try {
                    var evt = new MouseEvent('mousemove', {
                        bubbles: true,
                        cancelable: true,
                        movementX: ${dx.toInt()},
                        movementY: ${dy.toInt()},
                        clientX: window.innerWidth / 2,
                        clientY: window.innerHeight / 2
                    });
                    document.dispatchEvent(evt);
                    var active = document.elementFromPoint(window.innerWidth / 2, window.innerHeight / 2) || document.body;
                    if (active) active.dispatchEvent(evt);
                    var targets = document.querySelectorAll('canvas, video, iframe');
                    for (var i = 0; i < targets.length; i++) {
                        targets[i].dispatchEvent(evt);
                    }
                } catch(e) {}
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    fun injectFullscreenStyling(webView: WebView?) {
        if (webView == null) return
        val js = """
            (function() {
                try {
                    var meta = document.querySelector('meta[name="viewport"]');
                    if (!meta) {
                        meta = document.createElement('meta');
                        meta.name = 'viewport';
                        document.head.appendChild(meta);
                    }
                    meta.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no';

                    var style = document.getElementById('fiveplay-custom-fs-style');
                    if (!style) {
                        style = document.createElement('style');
                        style.id = 'fiveplay-custom-fs-style';
                        style.innerHTML = `
                            html, body {
                                width: 100% !important;
                                height: 100% !important;
                                margin: 0 !important;
                                padding: 0 !important;
                                overflow: hidden !important;
                                touch-action: none !important;
                                background-color: #000000 !important;
                            }
                            canvas, video, iframe {
                                max-width: 100vw !important;
                                max-height: 100vh !important;
                                object-fit: contain !important;
                                margin: auto !important;
                            }
                        `;
                        document.head.appendChild(style);
                    }
                } catch(e) {}
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }
}

(function () {
    var $location = $(location);
    var $window = $(window);
    var $exampleSelector = $("#input-code-example-select");
    var guid = guid();

    function hashChangeEventHandler() {
        var hash = $location.attr('hash');
        if (hash) {
            var decompressed = LZString.decompressFromEncodedURIComponent(hash.substring(1));
            if (decompressed) {
                inputEditor.getDoc().setValue(decompressed);
            }
            $exampleSelector.val("");
        } else {
            $exampleSelector.val("/console/examples/get-environment-info.groovy");
        }
        $exampleSelector.trigger("change");
    }

    var inputEditor = CodeMirror.fromTextArea(document.getElementById("input-code"), {
        mode: "text/x-groovy",
        lineNumbers: true,
        theme: "ambiance",
        matchBrackets: true,
        indentUnit: 2
    });

    var resultEditor = CodeMirror.fromTextArea(document.getElementById("result-code"), {
        mode: "application/x-json",
        lineNumbers: true,
        theme: "ambiance",
        matchBrackets: true,
        indentUnit: 2,
        readOnly: true
    });

    $("#send-button").on("click", function () {
        $.blockUI();
        $.ajax({
            url: "/consoleSocket/groovy",
            type: "POST",
            data: {userId:guid, script: inputEditor.getValue()}
        }).done(function (data) {
            resultEditor.getDoc().setValue(JSON.stringify(data, null, 2));
        }).fail(function () {
            resultEditor.getDoc().setValue("Failed to send request.");
        }).always(function () {
            $.unblockUI();
        });
    });

    $exampleSelector.on("change", function () {
        var selectedScript = $(this).val();
        if (selectedScript) {
            $.blockUI();
            $.ajax({
                url: selectedScript,
                mimeType: "text/x-groovy"
            }).done(function (data) {
                inputEditor.getDoc().setValue(data);
            }).fail(function () {
                resultEditor.getDoc().setValue("Failed to load example.");
            }).always(function () {
                $.unblockUI();
            });
        }
    });

    var socket = null;
    /**
     * 开启事务
     */
    $("#beginTransaction-button").on("click", function () {
        // var href = $location.attr('href').replace(/#.*/g, '');
        // resultEditor.getDoc().setValue(href + '#' + LZString.compressToEncodedURIComponent(inputEditor.getValue()));
        if (typeof(WebSocket) == "undefined") {
            console.log("您的浏览器不支持WebSocket");
        } else {
            console.log("您的浏览器支持WebSocket");
            //实现化WebSocket对象，指定要连接的服务器地址与端口  建立连接
            var userId = guid;
            var socketUrl = "ws://127.0.0.1:8080/webSocket/" + userId;
            console.log(socketUrl);
            if (socket != null) {
                socket.close();
                socket = null;
            }
            socket = new WebSocket(socketUrl);
            //打开事件
            socket.onopen = function () {
                console.log("websocket已打开");
                socket.send("beginTransaction");
            };
            //获得消息事件
            socket.onmessage = function (msg) {
                var serverMsg = "收到服务端信息：" + msg.data;
                console.log(serverMsg);
                //发现消息进入    开始处理前端触发逻辑
            };
            //关闭事件
            socket.onclose = function () {
                console.log("websocket已关闭");
            };
            //发生了错误事件
            socket.onerror = function () {
                console.log("websocket发生了错误");
            }
        }
    });

    /**
     * 提交事务
     */
    $("#commitTransaction-button").on("click", function () {
        if (socket == null) {
            return;
        }
        socket.send("commitTransaction");
    });

    /**
     * 回滚事务
     */
    $("#rollBackTransaction-button").on("click", function () {
        if (socket == null) {
            return;
        }
        socket.send("rollBackTransaction");
    });

    $window.on('hashchange', hashChangeEventHandler);
    $window.trigger('hashchange');

    //生成随机 GUID 数
    function guid() {
        function S4() {
            return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
        }
        return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
    }
})();

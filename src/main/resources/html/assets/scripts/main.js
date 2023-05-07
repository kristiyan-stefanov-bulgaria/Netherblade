window.onload = function () {
    fetch('http://localhost:35199/v1/client/available')
        .then((response) => response.json())
        .then((data) => {
            const dropdown = document.getElementById('regions');
            data['regions'].sort();
            data['regions'].forEach((region) => {
                let option = document.createElement("option");
                option.innerHTML = region;
                option.value = region;
                dropdown.appendChild(option);
            })
        })
        .catch((error) => {
            console.error('Error:', error);
        });
    connect("ws://127.0.0.1:8887");
    document.addEventListener("click", e => {
        if (e.target.matches('.fa-chevron-down') || e.target.matches('.fa-chevron-up')) {
            flip(e.target.parentNode);
        }
        if (e.target.matches('.expand')) {
            flip(e.target)
        }
    });
    var search = document.getElementById('search');
    search.addEventListener('keyup', filter);

    var methodsFilter = document.getElementById('methodsFilter');
    methodsFilter.addEventListener('change', methodsFilterHandler);
}

function call(url) {
    fetch(url)
        .catch((error) => {
            console.error('Error:', error);
        });
}

function dispose() {
    call('http://localhost:35199/v1/config/close');
}

function config() {
    call('http://localhost:35199/v1/config/load');
}

function wipe() {
    document.getElementById('display').innerHTML = "";
}

function methodsFilterHandler() {
    const methodsFilter = document.getElementById('methodsFilter');
    const display = document.getElementById('display');
    const children = Array.from(display.childNodes);
  
    children.forEach((child) => {
      if (child.outerHTML === undefined) return;
      const method = child.querySelector('.method').innerHTML.toLowerCase();
      const shouldShow = method === methodsFilter.value.toLowerCase() || methodsFilter.value.toLowerCase() === 'all';
      toggleHiddenClass(child, shouldShow);
    });
}

function filter() {
    const display = document.getElementById('display');
    const children = Array.from(display.childNodes);
    const query = search.value.toLowerCase();

    children.forEach((child) => {
        if (child.outerHTML === undefined) return;
        const source = child.outerHTML.toLowerCase();
        const shouldShow = source.includes(query);
        toggleHiddenClass(child, shouldShow);
    });
}

function toggleHiddenClass(element, shouldShow) {
    if (shouldShow) {
        if (element.classList.contains('hidden')) {
            element.classList.remove('hidden');
        }
    } else {
        if (!element.classList.contains('hidden')) {
            element.classList.add('hidden');
        }
    }
}
  
function flip(e) {
    for (const child of e.children) {
        child.classList.toggle('hidden');
    }
    const mainhint = e.parentNode; //main-hint
    const hint = mainhint.parentNode; //hint
    const bonushint = hint.lastElementChild;
    const visible = bonushint.classList.contains("hidden");
    if (visible) {
        mainhint.style = "border-radius: 8px 8px 0 0";
    } else {
        mainhint.style = "";
    }
    bonushint.classList.toggle('hidden');
}

function launch() {
    const region = document.getElementById('regions').value;
    fetch('http://localhost:35199/v1/client/launch/' + region)
        .then((response) => response.text())
        .then((text) => {
            console.log(text);
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function connect(host) {
    let socket = new WebSocket(host);
    socket.onopen = function (msg) {
        console.log("Connected to " + host);
    };
    socket.onmessage = function (msg) {
        const json = JSON.parse(msg.data);
        if (json['protocol'] === 'http') {
            appendHTML(json);
        } else if (json['protocol'] === 'rtmp') {
            appendRTMP(json);
        } else if (json['protocol'] === 'xmpp') {
            appendXMPP(json);
        } else if (json['protocol'] === 'rms') {
            appendRMS(json);
        } else {
            console.log("unknown protocol: " + json['protocol']);
        }
        filter();
        methodsFilterHandler();
    };
    socket.onclose = function (msg) {
        console.log("disconnected from " + host);
    };
}

function appendXMPP(json) {
    appendSpecial('XMPP', json);
}

function appendRTMP(json) {
    appendSpecial('RTMP', json);
}

function appendRMS(json) {
    appendSpecial('RMS', json);
}

function appendSpecial(name, json) {
    const request = document.createElement("div");
    request.className = "request";
    let ingoing = json.hasOwnProperty("in");
    let value = json[ingoing ? 'in' : 'out'];
    request.appendChild(hintSpecial(name, ingoing, value));
    const display = document.getElementById('display');
    display.insertBefore(request, display.firstChild);
}

function hintSpecial(name, ingoing, value) {
    const hint = document.createElement("div");
    hint.className = "hint";
    const main = document.createElement("div");
    main.className = "main-hint";
    const method = document.createElement("div");
    method.className = "method";
    method.innerHTML = name;
    main.appendChild(method);
    const uri = document.createElement("div");
    uri.className = "uri";
    switch (name) {
        case "RMS":
            let resource = value['payload'].hasOwnProperty("resource");
            uri.innerHTML = resource ? value['subject'] + " " + value['payload']['resource'] : value['subject'];
            break;
        case "XMPP":
            let regex = /id=\"(.*?)\"/g;
            let match = regex.exec(value);
            if (match) {
                uri.innerHTML = match[1];
            } else {
                regex = /^<\/?(.*?)[. >]/g;
                match = regex.exec(value);
                if (match) {
                    uri.innerHTML = match[1];
                } else {
                    uri.innerHTML = "heartbeat";
                }
            }
            break;
        case "RTMP":
            switch (value['result']) {
                case "receive":
                    let response = value['data']['flex.messaging.messages.AsyncMessage']['body']['com.riotgames.platform.serviceproxy.dispatch.LcdsServiceProxyResponse']
                    uri.innerHTML = "receive: " + response['serviceName'] + " " + response['methodName'] + " " + response['messageId'];
                    break;
                case "_result":
                    uri.innerHTML = "result id: " + value['invokeId'];
                    break;
                default:
                    let data = value['data'];
                    uri.innerHTML = "invoke id: " + value['invokeId'] + " " + data['destination'] + " " + data['operation'];
                    break;

            }
            break;
    }
    main.appendChild(uri);
    const code = document.createElement("div");
    code.className = "code";
    code.innerHTML = ingoing ? 'IN' : 'OUT';
    main.appendChild(code);
    main.appendChild(expand())
    hint.appendChild(main);
    hint.appendChild(specialBonus(value))
    return hint;
}

function specialBonus(value) {
    const bonus = document.createElement("div");
    bonus.className = "bonus-hint hidden";
    bonus.appendChild(content(value));
    return bonus;
}

function content(value) {
    const center = document.createElement("div");
    center.className = "data";
    const body = document.createElement("div");
    body.className = "request-group";
    body.appendChild(header("Content", "cp-body"));
    const text = document.createElement("pre");
    text.className = "request-value";
    switch (encoding(value)) {
        case "json":
            text.textContent = JSON.stringify(value, null, 2);
            break;
        case "xml":
            text.textContent = formatXml(value);
            break
        default:
            text.textContent = value;
            break
    }
    body.appendChild(text);
    center.appendChild(body);
    return center;
}

function appendHTML(json) {
    const request = document.createElement("div");
    request.className = "request";
    request.appendChild(hint(json['request'], json['received']));
    const display = document.getElementById('display');
    display.insertBefore(request, display.firstChild);
}

function hint(request, response) {
    const hint = document.createElement("div");
    hint.className = "hint";
    const main = document.createElement("div");
    main.className = "main-hint";
    const method = document.createElement("div");
    method.className = "method";
    method.innerHTML = request['method'];
    main.appendChild(method);
    const uri = document.createElement("div");
    uri.className = "uri";
    uri.innerHTML = request['uri'];
    main.appendChild(uri);
    const code = document.createElement("div");
    code.className = "code";
    code.innerHTML = response['code'];
    main.appendChild(code);
    main.appendChild(expand())
    hint.appendChild(main);
    hint.appendChild(bonus(request, response))
    return hint;
}

function bonus(request, response) {
    const bonus = document.createElement("div");
    bonus.className = "bonus-hint hidden";
    bonus.appendChild(left(request));
    bonus.appendChild(right(response));
    return bonus;
}

function header(title, type) {
    const header = document.createElement("div");
    header.classList = "noselect group-name " + type;
    header.innerHTML = title;
    return header;
}

function left(request) {
    const left = document.createElement("div");
    left.className = "requestdata";
    left.appendChild(header("REQUEST", "group-main group-left"));
    const parameters = document.createElement("div");
    parameters.className = "request-group";
    parameters.appendChild(header("QUERY", "cp-query"));
    const params = request['query'];
    for (var i = 0; i < params.length; i++) {
        let key = params[i]['k'];
        let value = params[i]['v'];
        const param = document.createElement("div");
        param.className = "request-value";
        param.innerHTML = key + ": " + value;
        parameters.appendChild(param);
    }
    left.appendChild(parameters);
    const headers = document.createElement("div");
    headers.className = "request-group";
    headers.appendChild(header("HEADERS", "cp-headers"));
    const fields = request['headers'];
    for (var i = 0; i < fields.length; i++) {
        let key = fields[i]['k'];
        let value = fields[i]['v'];
        const header = document.createElement("div");
        header.className = "request-value";
        header.innerHTML = key + ": " + value;
        headers.appendChild(header);
    }
    left.appendChild(headers);
    const body = document.createElement("div");
    body.className = "request-group";
    body.appendChild(header("BODY", "cp-body"));
    const text = document.createElement("pre");
    text.className = "request-value";
    let value = request['body'];
    switch (encoding(value)) {
        case "json":
            text.textContent = JSON.stringify(JSON.parse(value), null, 2);
            break;
        case "xml":
            text.textContent = formatXml(value);
            break
        default:
            text.textContent = value;
            break
    }
    body.appendChild(text);
    left.appendChild(body);
    return left;
}

function right(response) {
    const right = document.createElement("div");
    right.className = "responsedata";
    right.appendChild(header("RESPONSE", "group-main group-right"));
    const headers = document.createElement("div");
    headers.className = "request-group";
    headers.appendChild(header("HEADERS", "cp-headers"));
    const fields = response['headers'];
    for (var i = 0; i < fields.length; i++) {
        let key = fields[i]['k'];
        let value = fields[i]['v'];
        const header = document.createElement("div");
        header.className = "request-value";
        header.innerHTML = key + ": " + value;
        headers.appendChild(header);
    }
    right.appendChild(headers);
    const body = document.createElement("div");
    body.className = "request-group";
    body.appendChild(header("BODY", "cp-body"));
    const text = document.createElement("pre");
    text.className = "request-value";
    let value = response['body'];
    switch (encoding(value)) {
        case "json":
            text.textContent = JSON.stringify(JSON.parse(value), null, 2);
            break;
        case "xml":
            text.textContent = formatXml(value);
            break
        default:
            text.textContent = value;
            break
    }
    body.appendChild(text);
    right.appendChild(body);
    return right;
}

function expand() {
    const expand = document.createElement("div");
    expand.className = "expand";
    const down = document.createElement("i");
    down.classList = "fa-solid fa-chevron-down";
    expand.appendChild(down);
    const up = document.createElement("i");
    up.classList = "fa-solid fa-chevron-up hidden";
    expand.appendChild(up);
    return expand;
}

function encoding(o) {
    if (o.length === 0) return "plain";
    else if (JSON.stringify(o).charAt(0) === '{') return "json";
    else if (o.charAt(0) === '{') return "json";
    else if (o.charAt(0) === '<') return "xml";
    else return "plain";
}

function formatXml(xml) {
    var formatted = '';
    var reg = /(>)(<)(\/*)/g;
    xml = xml.toString().replace(reg, '$1\r\n$2$3');
    var pad = 0;
    var nodes = xml.split('\r\n');
    for (var n in nodes) {
        var node = nodes[n];
        var indent = 0;
        if (node.match(/.+<\/\w[^>]*>$/)) {
            indent = 0;
        } else if (node.match(/^<\/\w/)) {
            if (pad !== 0) {
                pad -= 1;
            }
        } else if (node.match(/^<\w[^>]*[^\/]>.*$/)) {
            indent = 1;
        } else {
            indent = 0;
        }

        var padding = '';
        for (var i = 0; i < pad; i++) {
            padding += '  ';
        }

        formatted += padding + node + '\r\n';
        pad += indent;
    }
    return formatted;
}
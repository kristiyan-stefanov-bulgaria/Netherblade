window.onload = function () {
    fetch('http://localhost:35199/v1/client/available')
        .then((response) => response.json())
        .then((data) => {
            const dropdown = document.getElementById('regions');
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

function filter() {
    var display = document.getElementById('display');
    var children = display.childNodes;
    var query = search.value.toLowerCase();
    for (var i = 0; i < children.length; i++) {
        var ref = children[i];
        if (ref.outerHTML === undefined) continue;
        var source = ref.outerHTML.toLowerCase();
        if (source.includes(query)) {
            if (ref.classList.contains("hidden")) ref.classList.remove("hidden")
        } else {
            if (!ref.classList.contains("hidden")) ref.classList.add("hidden")
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
        append(JSON.parse(msg.data));
        filter();
    };
    socket.onclose = function (msg) {
        console.log("disconnected from " + host);
    };
}

function append(json) {
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
    const text = document.createElement("div");
    text.className = "request-value";
    text.innerHTML = request['body'];
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
    const text = document.createElement("div");
    text.className = "request-value";
    text.innerHTML = response['body'];
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
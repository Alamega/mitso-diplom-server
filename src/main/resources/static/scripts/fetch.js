function getList(url) {
    fetch(url).then((response) => response.json()).then((result) => {
        for (let i = 0; i < result.length; i++) {
            document.getElementById("el").insertAdjacentHTML("beforeend", "<p style='color: green'>" + result[i] + "</p>")
        }
    });
}

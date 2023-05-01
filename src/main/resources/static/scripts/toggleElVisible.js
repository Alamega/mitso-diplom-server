function toggleById(id) {
    let el = document.getElementById(id);
    if (el.style.display === "none") {
        el.style.display = ""
    } else {
        el.style.display = "none";
    }
}
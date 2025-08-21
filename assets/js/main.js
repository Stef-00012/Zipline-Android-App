document.addEventListener("DOMContentLoaded", () => {
    const versionElement = document.getElementById("appVersion")
    const currentYearElement = document.getElementById("currentYear");
	
    if (currentYearElement) currentYearElement.innerText = new Date().getFullYear();

    fetch("https://api.github.com/repos/Stef-00012/Zipline-Android-App/releases/latest")
        .then(res => res.json())
        .then(data => {
            const version = data.tag_name.split("-").shift();

            versionElement.innerText = version;
        })
});
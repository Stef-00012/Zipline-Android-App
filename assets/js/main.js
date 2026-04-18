document.addEventListener("DOMContentLoaded", () => {
    const versionElement = document.getElementById("appVersion")
    const currentYearElement = document.getElementById("currentYear");
	
    if (currentYearElement) currentYearElement.innerText = new Date().getFullYear();

    fetch("https://git.stefdp.com/api/v1/repos/Stef/Zipline-Android-App/releases/latest")
        .then(res => res.json())
        .then(data => {
            console.log(data);
            versionElement.innerText = data.tag_name;
        })
});
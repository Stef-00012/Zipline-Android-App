const container = document.querySelector(".tooltip-container");
const tooltip = container.querySelector(".tooltip-content");
const target = container.querySelector(".tooltip-target");

function showTooltip() {
	tooltip.style.visibility = "visible";
	tooltip.style.opacity = "1";
}
function hideTooltip() {
	tooltip.style.visibility = "";
	tooltip.style.opacity = "";
}

target.addEventListener("focus", showTooltip);
container.addEventListener("focus", showTooltip);

target.addEventListener("blur", hideTooltip);
container.addEventListener("blur", hideTooltip);

document.addEventListener("touchstart", (e) => {
	if (!container.contains(e.target)) hideTooltip();
});

target.addEventListener("touchend", (e) => {
	e.preventDefault();

	if (tooltip.style.visibility === "visible") hideTooltip();
	else showTooltip();
});

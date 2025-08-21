const carousel = document.getElementById('app-carousel');
const track = carousel.querySelector('.carousel-track');
const images = Array.from(track.children);
const prevBtn = carousel.querySelector('.carousel-arrow.left');
const nextBtn = carousel.querySelector('.carousel-arrow.right');
const indicators = carousel.querySelector('.carousel-indicators');

let current = 0;
let animating = false;

indicators.innerHTML = images.map((_, i) => `<button class="carousel-dot" aria-label="Go to image ${i+1}"></button>`).join('');
const dots = Array.from(indicators.children);

function update() {
    images.forEach((img, i) => {
        img.classList.toggle('active', i === current);
        img.style.zIndex = i === current ? 1 : 0;
    });

    dots.forEach((dot, i) => {
        dot.classList.toggle('active', i === current);
    });
}

function goTo(idx) {
    if (animating) return;

    const prev = current;
    current = (idx + images.length) % images.length;

    if (prev !== current) {
        animating = true;

        images[prev].classList.remove('active');
        images[current].classList.add('active', 'fade-in');

        setTimeout(() => {
            images[current].classList.remove('fade-in');
            animating = false;
        }, 350);
    }

    update();
}
prevBtn.addEventListener('click', () => goTo(current - 1));
nextBtn.addEventListener('click', () => goTo(current + 1));

dots.forEach((dot, i) => dot.addEventListener('click', () => goTo(i)));

let startX = null;

track.addEventListener('touchstart', e => {
    startX = e.touches[0].clientX;
});

track.addEventListener('touchend', e => {
    if (startX === null) return;

    const dx = e.changedTouches[0].clientX - startX;

    if (Math.abs(dx) > 40) {
        if (dx < 0) goTo(current + 1);
        else goTo(current - 1);
    }

    startX = null;
});

images.forEach((img) => {
    img.style.display = 'block'
});

images.forEach((img, i) => {
    img.classList.toggle('active', i === 0)
});

update();
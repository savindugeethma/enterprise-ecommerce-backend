window.addEventListener("load", async () => {
    Notiflix.Loading.pulse("Wait...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });
    try {
        // await loadAllBrands();
        console.log("index.js called");
        await loadNewArrivals();
        await loadTabContent();
    } finally {
        Notiflix.Loading.remove();
    }
});

async function loadNewArrivals() {
    try {
        const response = await fetch("api/data/new-arrivals");
        if (response.ok) {
            const data = await response.json();
            renderingNewArrivals(data.newArrivals);
            // console.log(data);
        } else {
            Notiflix.Notify.failure("Product data loading failed!", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

// function renderingNewArrivals(productList) {
//     console.log("rendering New Arrivals");
//     // console.log(productList);
//
//     const newArrivalProductContainer = document.getElementById("newArrivalRow");
//     newArrivalProductContainer.innerHTML = "";
//
//     productList.forEach((product) => {
//
//         if (Array.isArray(product.stockDTOList)) {
//             product.stockDTOList.forEach((stock) => {
//                 console.log("rendering now");
//
//                 newArrivalProductContainer.innerHTML += `
//                 <div class="col-lg-3 col-sm-6">
//                     <div class="single-product-02">
//                         <div class="product-images">
//                             <a href="product-details.html">
//                                 <img src="assets/images/product/product-03.jpg" width="270" height="303">
//                             </a>
//                         </div>
//                         <div class="product-content">
//                             <h4 class="title">${product.name ?? 'No Name'}</h4>
//                             <div class="price">
//                                 <span class="sale-price">${stock.price ?? '0.00'}</span>
//                             </div>
//                         </div>
//                     </div>
//                 </div>
//
//                 `;
//             });
//         } else {
//             console.warn("stockDTOList not found for product:", product);
//         }
//     });
//
//     initializeNewArrivalSlider();
// }

function renderingNewArrivals(productList) {
    const container = document.getElementById("newArrivalRow");
    container.innerHTML = ""; // clear existing products

    productList.forEach(product => {
                console.log(productList);

        const image =
            Array.isArray(product.images) && product.images.length > 0
                ? product.images[0]
                : "assets/images/product/product-03.jpg";

        const productHTML = `
        <div class="col-lg-3 col-sm-6">
            <div class="single-product-02">
                <div class="product-images">
                    <a href="product-details-sticky.html?productId=${product.stockId}">
                        <img src="${image}" width="270" height="303"
                             alt="${product.title}" />
                    </a>

                    <ul class="product-meta">
                        <li><a class="action" data-bs-toggle="modal" data-bs-target="#quickView"><i class="pe-7s-search"></i></a></li>
                        <li><a class="action"><i class="pe-7s-shopbag" onclick="addToCart(${product.stockId},1);"></i></a></li>
                        <li><a class="action"><i class="pe-7s-like"></i></a></li>
                    </ul>
                </div>

                <div class="product-content">
                    <h4 class="title">
                        <a href="product-details.html">${product.title}</a>
                    </h4>
                    <div class="price">
                        <span class="sale-price">Rs. ${product.price}</span>
                    </div>
                </div>
            </div>
        </div>
    `;

        container.insertAdjacentHTML("beforeend", productHTML);
    });


    initializeNewArrivalSlider(); // Initialize or refresh Slick
}

// function renderingNewArrivals(productList) {
//     console.log("rendering New Arrivals");
//
//     const newArrivalProductContainer = document.getElementById("newArrivalRow");
//     newArrivalProductContainer.innerHTML = "";
//
//     productList.forEach((product) => {
//
//         newArrivalProductContainer.innerHTML += `
//         <div class="col-lg-3 col-sm-6">
//             <div class="single-product-02">
//                 <div class="product-images">
//                     <a href="product-details.html">
//                         <img src="assets/images/product/product-03.jpg" width="270" height="303">
//                     </a>
//                 </div>
//
//                 <div class="product-content">
//                     <h4 class="title">${product.title}</h4>
//                     <div class="price">
//                         <span class="sale-price">Rs. ${product.price}</span>
//                     </div>
//                     <div class="qty">Qty: ${product.qty}</div>
//                     <div class="color">Color: ${product.colorValue}</div>
//                 </div>
//             </div>
//         </div>`;
//     });
//
//     refreshAnimations();
// }

function initializeNewArrivalSlider() {
    // Check if jQuery and Slick are loaded
    if (typeof $ === "undefined" || typeof $.fn.slick === "undefined") return;

    const $slider = $('#newArrivalRow');

    // Initialize Slick if not already initialized
    if (!$slider.hasClass('slick-initialized')) {
        $slider.slick({
            slidesToShow: 4,
            slidesToScroll: 1,
            arrows: true,
            dots: false,
            infinite: true,
            responsive: [
                { breakpoint: 1024, settings: { slidesToShow: 3 } },
                { breakpoint: 768, settings: { slidesToShow: 2 } },
                { breakpoint: 480, settings: { slidesToShow: 1 } }
            ]
        });
    } else {
        // Refresh slider if already initialized
        $slider.slick('refresh');
    }

    // Reinitialize scroll animations if SAL.js is loaded
    if (typeof sal === "function") {
        sal();
    }
}

$('a[data-bs-toggle="tab"]').on('shown.bs.tab', function () {
    initializeNewArrivalSlider();
});

$(document).ready(function () {
    initializeNewArrivalSlider();
});

// async function loadAllBrands() {
//     try {
//         const productBrandContainer = document.getElementById("product-brand-container");
//         productBrandContainer.innerHTML = "";
//         const response = await fetch("api/data/brands");
//         if (response.ok) {
//             const data = await response.json();
//             data.brands.forEach((brand) => {
//                 productBrandContainer.innerHTML += `<div class="slick-single-layout" id="product-brand-card">
//                     <div class="categrie-product" data-sal="zoom-out" data-sal-delay="200" data-sal-duration="500"
//                          id="product-brand-mini-card">
//                         <a href="search.html?brand=${brand.id}&name=${brand.name}" id="product-brand-a">
//                             <img class="img-fluid" src="./assets/images/product/categories/sample.png"
//                                  alt="product categorie">
//                             <h6 class="cat-title" id="product-brand-title">${brand.name}</h6>
//                         </a>
//                     </div>
//                     <!-- End .categrie-product -->
//                 </div>`
//             });
//             refreshAnimations();
//
//         } else {
//             Notiflix.Notify.failure("Brands loading failed!", {
//                 position: 'center-top'
//             });
//         }
//     } catch (e) {
//         Notiflix.Notify.failure(e.message, {
//             position: 'center-top'
//         });
//     }
// }

async function loadTabContent() {
    try {
        const response = await fetch("api/data/tab-content");
        if (response.ok) {
            const data = await response.json();

            // 📦 DB එකෙන් එන සම්පූර්ණ ප්‍රොඩක්ට් ලිස්ට් එක (data.newArrivals)
            const allProducts = data.newArrivals;
            console.log("📦 Total Products loaded into index:", allProducts.length);

            if (allProducts && allProducts.length > 0) {

                // 1
                // Best Sellers (Tab 4):
                const bestSellers = allProducts.slice(0, 3);
                renderingTabContent(bestSellers, 'best-sellers-slider');

                // New Arrivals (Tab 5):
                const newArrivals = allProducts.slice(3, 6);
                renderingTabContent(newArrivals, 'new-arrivals-slider');

                // Top Rated (Tab 6):
                const topRated = allProducts.slice(6, 9);
                renderingTabContent(topRated, 'top-rated-slider');

                // 2
                const gridAll = allProducts.slice(0, 8);
                renderingNewGridContent(gridAll, 'new-grid-all');

                //Tab 3 (JUST NOW):
                const gridJustNow = allProducts.slice(8, 16);
                renderingNewGridContent(gridJustNow, 'new-grid-justnow');

                //Tab 4 (NEW ARRIVAL):
                const gridArrival = allProducts.slice(16, 24);
                renderingNewGridContent(gridArrival, 'new-grid-arrival');

                //Tab 5 (TOP SEALS):
                const gridTopSeals = allProducts.slice(24, 32);
                renderingNewGridContent(gridTopSeals, 'new-grid-topseals');

                // Tab 7:
                const tab7Products = allProducts.slice(0, 4);
                renderingTabContent(tab7Products, 'new-slider-tab7');

                // Tab 8:
                const tab8Products = allProducts.slice(4, 8);
                renderingTabContent(tab8Products, 'new-slider-tab8');

                // Tab 9:
                const tab9Products = allProducts.slice(8, 12);
                renderingTabContent(tab9Products, 'new-slider-tab9');

                // [CRITICAL]: Swiper Slider
                setTimeout(() => {
                    if (typeof Swiper !== 'undefined') {
                        new Swiper('.swiper-container', {
                            slidesPerView: 3,
                            spaceBetween: 30,
                            loop: true,

                        });
                    }
                }, 300);

            }

        } else {
            Notiflix.Notify.failure("Product data loading failed!", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

function renderingTabContent(products, containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.innerHTML = "";

    if (!products || products.length === 0) {
        const emptyHtml = `
            <div class="col-12 text-center my-5 py-4">
                <div style="font-size: 40px; color: #ccc; margin-bottom: 15px;">
                    <i class="pe-7s-box2"></i>
                </div>
                <h5 style="font-family: inherit; font-weight: 500; color: #555; letter-spacing: 1px;">
                    Fresh items are arriving soon!
                </h5>
                <p style="color: #888; font-size: 14px;">
                    We are currently updating our latest collection. Check back shortly.
                </p>
            </div>
        `;
        container.insertAdjacentHTML("beforeend", emptyHtml);
        return;
    }

    products.forEach(product => {
        const image = Array.isArray(product.images) && product.images.length > 0
            ? product.images[0]
            : "assets/images/product/product-03.jpg";

        const slideHtml = `
            <div class="swiper-slide">
                <div class="single-product-02">
                    <div class="product-images">
                        <a href="product-details-sticky.html?productId=${product.stockId}">
                            <img src="${image}" width="270" height="303" alt="${product.title}" />
                        </a>

                        <ul class="product-meta">
                            <li><a class="action" data-bs-toggle="modal" data-bs-target="#quickView"><i class="pe-7s-search"></i></a></li>
                            <li><a class="action"><i class="pe-7s-shopbag" onclick="addToCart(${product.stockId},1);"></i></a></li>
                            <li><a class="action"><i class="pe-7s-like"></i></a></li>
                        </ul>
                    </div>

                    <div class="product-content">
                        <h4 class="title">
                            <a href="product-details-sticky.html?productId=${product.stockId}">${product.title}</a>
                        </h4>
                        <div class="price">
                            <span class="sale-price">Rs. ${product.price}</span>
                        </div>
                    </div>
                </div>
            </div>
        `;

        container.insertAdjacentHTML("beforeend", slideHtml);
    });
}

function renderingNewGridContent(products, containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.innerHTML = ""; //remove old dummy

    if (!products || products.length === 0) {
        const emptyHtml = `
            <div class="col-12 text-center my-5 py-4">
                <div style="font-size: 40px; color: #ccc; margin-bottom: 15px;">
                    <i class="pe-7s-box2"></i>
                </div>
                <h5 style="font-family: inherit; font-weight: 500; color: #555; letter-spacing: 1px;">
                    Fresh items are arriving soon!
                </h5>
                <p style="color: #888; font-size: 14px;">
                    We are currently updating our latest collection. Check back shortly.
                </p>
            </div>
        `;
        container.insertAdjacentHTML("beforeend", emptyHtml);
        return;
    }

    products.forEach(product => {
        const image = Array.isArray(product.images) && product.images.length > 0
            ? product.images[0]
            : "assets/images/product/product-03.jpg";

        const gridHtml = `
            <div class="col-lg-3 col-sm-6 mb-4">
                <div class="single-product-02">
                    <div class="product-images">
                        <a href="product-details-sticky.html?productId=${product.stockId}">
                            <img src="${image}" width="270" height="303" alt="${product.title}" />
                        </a>
                        <ul class="product-meta">
                            <li><a class="action" data-bs-toggle="modal" data-bs-target="#quickView"><i class="pe-7s-search"></i></a></li>
                            <li><a class="action"><i class="pe-7s-shopbag" onclick="addToCart(${product.stockId},1);"></i></a></li>
                            <li><a class="action"><i class="pe-7s-like"></i></a></li>
                        </ul>
                    </div>
                    <div class="product-content">
                        <h4 class="title">
                            <a href="product-details-sticky.html?productId=${product.stockId}">${product.title}</a>
                        </h4>
                        <div class="price">
                            <span class="sale-price">Rs. ${product.price}</span>
                        </div>
                    </div>
                </div>
            </div>
        `;
        container.insertAdjacentHTML("beforeend", gridHtml);
    });
}
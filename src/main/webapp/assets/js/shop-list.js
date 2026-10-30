// document.addEventListener("DOMContentLoaded", () => {
//     getAllProductsInitially();
// });

async function getAllProductsInitially() {
    try {
        console.log("Loading all products initially...");

        const response = await fetch('api/data/all-products');

        if (response.status === 401) {
            window.location.href = '/unauthorized.html';
            return;
        }

        if (response.ok) {
            const data = await response.json();
            console.log(data);
            renderAllProductsInitially(data)
        }
    } catch (e) {
        console.error("Error fetching initial products:", e);
    }
}

// function renderAllProductsInitially(data) {
//     // 1. "12 PRODUCT FOUND OF 30" වගේ count එක update කරනවා
//     // (HTML එකේ මේ count එක පෙන්වන element එකට id="shop-total-count" කියලා දීලා තියෙන්න ඕනේ)
//     const countElement = document.getElementById("shop-total-count");
//     if (countElement) {
//         countElement.innerText = `${data.allProducts.length} PRODUCT FOUND OF ${data.allProducts.length}`;
//     }
//
//     // 2. ප්‍රධාන product wrapper container එක අල්ලගන්නවා
//     const gridContainer = document.querySelector(".shop-product-wrapper");
//     if (!gridContainer) {
//         console.error("Error: .shop-product-wrapper container එක හොයාගන්න බැහැ මචන්!");
//         return;
//     }
//
//     gridContainer.innerHTML = ""; // දැනට තියෙන static product කාඩ් ටික සම්පූර්ණයෙන්ම clear කරනවා
//
//     // 3. Database එකෙන් ආපු Array එක ලූප් කරලා dynamic කාඩ් ටික හදනවා
//     data.allProducts.forEach(prod => {
//         const card = document.createElement("div");
//         card.className = "single-product-02 product-list"; // උඹේ template එකේ තියෙන පට්ටම ප්ලගින් classes දෙක
//
//         // ⚠️ පොඩි මතක් කිරීමක්: prod.images වල තියෙන්නේ assets/images/... වගේ path එකක් නේද කියලා ෂුවර් කරගනින්
//         card.innerHTML = `
//             <div class="product-images">
//                 <a href="#"><img src="${prod.images}" width="270" height="303" alt="${prod.title}" /></a>
//
//                 <ul class="product-meta">
//                     <li>
//                         <a class="action" data-bs-toggle="modal" data-bs-target="#quickView" href="#"><i class="pe-7s-search"></i></a>
//                     </li>
//                     <li>
//                         <a class="action" href="#"><i class="pe-7s-shopbag"></i></a>
//                     </li>
//                     <li>
//                         <a class="action" href="#"><i class="pe-7s-like"></i></a>
//                     </li>
//                 </ul>
//             </div>
//             <div class="product-content">
//                 <h4 class="title">
//                     <a href="product-details.html">${prod.title}</a>
//                 </h4>
//                 <div class="price">
//                     <span class="sale-price">$${Number(prod.price).toFixed(2)}</span>
//                 </div>
//                 <p>
//                     {/* මෙතනට උඹේ product එකේ description එකක් db එකේ තියෙනවා නම් prod.description වගේ දාන්න පුළුවන් */}
//                     Premium quality furniture item crafted with the finest materials for modern living.
//                 </p>
//             </div>
//         `;
//
//         gridContainer.appendChild(card);
//     });
//
//     console.log("UI Rendering Completed Successfully with 3 Products! ✅🔥");
// }

document.addEventListener("DOMContentLoaded", () => {

    fetchShopProducts();


    const searchBtn = document.getElementById("shop-search-btn");
    if (searchBtn) {
        searchBtn.addEventListener("click", () => {
            fetchShopProducts();
        });
    }

    // Input eke search btn pressed
    const searchInput = document.getElementById("shop-search-input");
    if (searchInput) {
        searchInput.addEventListener("keypress", (e) => {
            if (e.key === 'Enter') {
                fetchShopProducts();
            }
        });
    }
});

async function fetchShopProducts() {
    try {
        console.log("Fetching shop products with current active filters...");

        // 1. get search bar text
        const searchInput = document.getElementById("shop-search-input");
        const searchQuery = searchInput ? searchInput.value.trim() : "";

        // 2. HTML  checked  Categories, Colors, Sizes grap
        const selectedCategories = Array.from(document.querySelectorAll(".category-filter-chk:checked")).map(chk => chk.value);
        const selectedColors = Array.from(document.querySelectorAll(".color-filter-chk:checked")).map(chk => chk.value);
        const selectedSizes = Array.from(document.querySelectorAll(".size-filter-chk:checked")).map(chk => chk.value);

        // Create URL Query parameters
        const params = new URLSearchParams();
        if (searchQuery) params.append("search", searchQuery);
        if (selectedCategories.length > 0) params.append("categories", selectedCategories.join(","));
        if (selectedColors.length > 0) params.append("colors", selectedColors.join(","));
        if (selectedSizes.length > 0) params.append("sizes", selectedSizes.join(","));


        const response = await fetch(`api/data/all-products?${params.toString()}`);

        if (response.status === 401) {
            window.location.href = '/unauthorized.html';
            return;
        }

        if (response.ok) {
            const data = await response.json();
            console.log("Filtered Shop Data received: ", data);

            if (data.status) {

                renderShopProductsUI(data);
            } else {
                console.error("Backend Error: " + data.message);
            }
        }
    } catch (e) {
        console.error("Error sorting/filtering products:", e);
    }
}

function renderShopProductsUI(data) {
    // 1. Dynamic product seyt ("3 Product Found of")
    const countElement = document.getElementById("product-count");
    if (countElement) {
        countElement.innerText = data.allProducts.length;
    }


    const gridRowContainer = document.querySelector(".shop-product-wrapper .row");
    if (!gridRowContainer) {
        console.error("Error: .shop-product-wrapper .row doesnt find");
        return;
    }

    gridRowContainer.innerHTML = ""; // clear all static product

    // 3. rendering
    data.allProducts.forEach(prod => {

        const colDiv = document.createElement("div");
        colDiv.className = "col-lg-4 col-sm-6";

        colDiv.innerHTML = `
            <!-- Single Product Start -->
            <div class="single-product">
                <a href="#">
                    <img src="${prod.images && prod.images.length > 0 ? prod.images[0].replace(/\\/g, '/') : 'assets/images/default.png'}" width="270" height="303" alt="${prod.title}" />
                </a>
                
                <div class="product-content">
                    <h4 class="title">
                        <a href="product-details.html">${prod.title}</a>
                    </h4>
                    <div class="price">
                        <span class="sale-price">$${Number(prod.price).toFixed(2)}</span>
                    </div>
                </div>
                <ul class="product-meta">
                    <li>
                        <a class="action" data-bs-toggle="modal" data-bs-target="#quickView" href="#"><i class="pe-7s-search"></i></a>
                    </li>
                    <li>
                        <a class="action" href="#"><i class="pe-7s-shopbag"></i></a>
                    </li>
                    <li>
                        <a class="action" href="#"><i class="pe-7s-like"></i></a>
                    </li>
                </ul>
            </div>
            <!-- Single Product End -->
        `;

        gridRowContainer.appendChild(colDiv);
    });

    console.log("Grid UI updated dynamically with correct Bootstrap columns!");
}
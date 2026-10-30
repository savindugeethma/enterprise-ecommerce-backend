const parms = new URLSearchParams(window.location.search);
const productId = parms.get("productId");
console.log(productId);

window.addEventListener("load", async () => {
    try {
        Notiflix.Loading.pulse("Wait...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });

        await loadSingleProduct();

    } finally {
        Notiflix.Loading.remove();
    }

});

async function loadSingleProduct() {
    console.log("loading");
    try {
        const response = await fetch(`api/single-products/product?productId=${productId}`);
        if (response.ok) {
            const data = await response.json();
            console.log(data);

            if (data.status) {
                const product = data.singleProduct;

                const galleryRow = document.getElementById("product-gallery-row");
                if (galleryRow) {
                    galleryRow.innerHTML = "";
                    if (product.images && product.images.length > 0) {
                        product.images.forEach(imagePath => {
                            const imageCol = document.createElement("div");
                            imageCol.className = "col";

                            imageCol.innerHTML = `
                                <div class="single-gallery-image" style="margin-bottom: 20px; width: 100%;">
                                    <img src="${imagePath}" alt="${product.title}" class="img-fluid" style="width: 100%; height: auto; display: block; border-radius: 8px;" />
                                </div>
                            `;

                            galleryRow.appendChild(imageCol);
                        });
                    } else {
                        galleryRow.innerHTML = `
                            <div class="col">
                                <div class="single-gallery-image">
                                    <img src="assets/images/product-details/default.jpg" alt="No Image" class="img-fluid" />
                                </div>
                            </div>
                        `;
                    }
                }

                document.getElementById("product-title").innerHTML = product.title;
                document.getElementById("product-price").innerHTML = new Intl.NumberFormat("en-US", {
                    minimumFractionDigits: 2
                }).format(product.price);

                if(document.getElementById("brand-name") && product.brandName) {
                    document.getElementById("brand-name").innerHTML = product.brandName;
                }
                if(document.getElementById("model-name") && product.modelName) {
                    document.getElementById("model-name").innerHTML = product.modelName;
                }
                if(document.getElementById("product-quality") && product.qualityValue) {
                    document.getElementById("product-quality").innerHTML = product.qualityValue;
                }
                if(document.getElementById("product-category") && product.categoryName) {
                    document.getElementById("product-category").innerHTML = product.categoryName;
                }

                document.getElementById("product-description").innerHTML = product.description;

            } else {
                Notiflix.Notify.failure(data.message, { position: 'center-top' });
            }
        } else {
            Notiflix.Notify.failure("Single Product data loading failed!", { position: 'center-top' });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, { position: 'center-top' });
    }
}

// async function loadSingleProduct() {
//     console.log("loading");
//     try {
//         const response = await fetch(`api/single-products/product?productId=${productId}`);
//         if (response.ok) {
//             const data = await response.json();
//             console.log(data);
//
//             if (data.status) {
//                 const product = data.singleProduct;
//                 // product.images.forEach((image, index) => {
//                 //     let mainImg = document.getElementById(`image${index + 1}`);
//                 //     let thumbImg = document.getElementById(`thumb-image${index + 1}`);
//                 //     if (mainImg && thumbImg) {
//                 //         mainImg.src = image;
//                 //         thumbImg.src = image;
//                 //     }
//                 // });
//                 document.getElementById("product-title").innerHTML = product.title;
//                 // document.getElementById("published-on").innerHTML = product.createdAt;
//                 document.getElementById("product-price").innerHTML = new Intl.NumberFormat("en-US", {
//                     minimumFractionDigits: 2
//                 }).format(product.price);
//                 document.getElementById("brand-name").innerHTML = product.brandName;
//                 document.getElementById("model-name").innerHTML = product.modelName;
//                 document.getElementById("product-quality").innerHTML = product.qualityValue;
//                 // document.getElementById("product-stock").innerHTML = product.qty;
//                 //
//                 // // color variation
//                 // document.getElementById("color-border").style.borderColor = "black";
//                 // document.getElementById("color-background").style.backgroundColor = product.colorValue;
//                 //
//                 // // product storage
//                 // document.getElementById("product-storage").innerHTML = product.storageValue;
//                 // // description
//                 document.getElementById("product-description").innerHTML = product.description;
//                 //
//                 // const addToCartBtn = document.getElementById("add-to-cart-main"); // anchor tag -> prevent href
//                 // addToCartBtn.addEventListener("click", async (evt) => {
//                 //     const qtyInput = document.getElementById("add-to-cart-qty");
//                 //     await addToCart(product.stockId, qtyInput.value);
//                 //     evt.preventDefault();
//                 // });
//             } else {
//                 Notiflix.Notify.failure(data.message, {
//                     position: 'center-top'
//                 });
//             }
//         } else {
//             Notiflix.Notify.failure("Single Product data loading failed!", {
//                 position: 'center-top'
//             });
//         }
//     } catch (e) {
//         Notiflix.Notify.failure(e.message, {
//             position: 'center-top'
//         });
//     }
// }
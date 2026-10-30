async function addToCart(stockId, qty){
 console.log("added to cart");
    try {
        Notiflix.Loading.pulse("Wait...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });
        const response = await fetch(`api/carts/add-to-cart?sId=${stockId}&qty=${qty}`);
        if (response.ok) {
            const data = await response.json();
            if (data.status) {
                Notiflix.Notify.success(data.message, {
                    position: 'center-top'
                });
                await loadCartItems();
            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }
        } else {
            Notiflix.Notify.failure("Add to cart process failed!", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });

    } finally {
        Notiflix.Loading.remove();
    }
}

async function deleteCartItem(stockId) {
    try {
        Notiflix.Loading.pulse("Removing...", { clickToClose: false, svgColor: '#ef4444' });

        const response = await fetch(`api/carts/delete-cart-item?sId=${stockId}`);

        if (response.ok) {
            const data = await response.json();
            if (data.status) {
                Notiflix.Notify.success(data.message, { position: 'center-top' });
                await loadCartItems();
            } else {
                Notiflix.Notify.failure(data.message, { position: 'center-top' });
            }
        } else {
            Notiflix.Notify.failure("Failed to remove item!", { position: 'center-top' });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, { position: 'center-top' });
    } finally {
        Notiflix.Loading.remove();
    }
}

document.getElementById("cart-body").addEventListener("load", async  ()=>{
    await loadCartItems();
})

async function loadCartItems(){
    console.log("load cart");

    try {
        Notiflix.Loading.pulse("Wait...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });

        const response = await fetch("api/carts/all-carts");
        console.log(response);
        if (response.ok) {
            console.log(response);
            const data = await response.json();
            console.log(data);

            if (data.status) {
                console.log(data);
                Notiflix.Notify.success(data.message, {
                    position: 'center-top'
                });
                renderingMainPanel(data.cartItems);
                renderingSidePanel(data.cartItems);
            } else {
                window.location.replace("empty-cart.html");
            }
        } else {
            Notiflix.Notify.failure("Cart items loading failed!", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    } finally {
        Notiflix.Loading.remove();
    }
}

function renderingMainPanel(cartItems) {
    const cartItemContainer = document.getElementById("cart-item-container");
    if (cartItemContainer) {
        cartItemContainer.innerHTML = "";

        let total = 0;
        let totalQty = 0;

        cartItems.forEach((cart) => {
            let itemsTotal = parseFloat(cart.price) * parseInt(cart.qty);
            total += itemsTotal;
            totalQty += parseInt(cart.qty);

            let productImage = (cart.images && cart.images.length > 0) ? cart.images[0] : 'assets/images/product/default.jpg';

            cartItemContainer.innerHTML += `
                            <tr>
                                <td class="product-thumb">
                                    <a href="product-details_sticky.html?productId=${cart.stockId}">
                                        <img src="${productImage}" alt="${cart.productTitle}" class="img-fluid" style="width: 100px; height: auto; border-radius: 6px; object-fit: cover;" />
                                    </a>
                                </td>
                                <td class="product-info">
                                    <h6 class="name">
                                        <a href="product-details_sticky.html?productId=${cart.stockId}">${cart.productTitle}</a>
                                    </h6>
                                    <div class="product-prices">
                                        <span class="sale-price" style="color: #ff9900; font-weight: bold;">
                                            Rs. ${new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(cart.price)}
                                        </span>
                                    </div>
                                    <div class="product-size-color">
                                        <p>Type: <span>${cart.heavy ? 'Heavy Duty' : 'Standard'}</span></p>
                                    </div>
                                </td>
                                <td class="quantity">
                                    <div class="product-quantity d-inline-flex">
                                        <button type="button" class="sub" onclick="changeQuantity(${cart.cartId}, 'decrease')">-</button>
                                        <input type="text" id="qty-input-${cart.cartId}" value="${cart.qty}" readonly />
                                        <button type="button" class="add" onclick="changeQuantity(${cart.cartId}, 'increase')">+</button>
                                    </div>
                                </td>
                                <td class="product-total-price">
                                    <span class="price" style="font-weight: bold;">
                                        Rs. ${new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(itemsTotal)}
                                    </span>
                                </td>
                                <td class="product-action">
                                    <button class="remove" onclick="removeFromCart(${cart.cartId})">
                                        <i class="pe-7s-trash"></i>
                                    </button>
                                </td>
                            </tr> `;
        });

        document.getElementById("order-total-quantity").innerHTML = totalQty;
        document.getElementById("order-total-amount").innerHTML = "Rs. " + new Intl.NumberFormat("en-US",
            {minimumFractionDigits: 2}).format(total);
    }
}

async function renderingSidePanel(cartItems) {
    const side_panel_cart_item_list = document.getElementById("cart-content");
    if (side_panel_cart_item_list) {
        side_panel_cart_item_list.innerHTML = "";

        let total = 0;
        let totalQty = 0;

        cartItems.forEach(cart => {
            let productSubTotal = parseFloat(cart.price) * parseInt(cart.qty);
            total += productSubTotal;
            totalQty += parseInt(cart.qty);

            let productImage = (cart.images && cart.images.length > 0) ? cart.images[0] : 'assets/images/product/default.jpg';

            let cartItem = `<li>
                                <div class="single-cart-item">
                                    <div class="cart-thumb">
                                        <a href="product-details_sticky.html?productId=${cart.stockId}">
                                            <img src="${productImage}" width="98" height="98" alt="${cart.productTitle}" style="object-fit: cover; border-radius: 4px;" />
                                        </a>
                                        <span class="product-quantity">${cart.qty}x</span>
                                    </div>
                                    <div class="cart-item-content">
                                        <h6 class="product-name">
                                            <a href="product-details_sticky.html?productId=${cart.stockId}">${cart.productTitle}</a>
                                        </h6>
                                        <span class="product-price" style="color: #ff9900; font-weight: bold;">
                                            Rs. ${new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(cart.price)}
                                        </span>
                                        <div class="attributes-content">
                                            <span><strong>Type:</strong> ${cart.heavy ? 'Heavy' : 'Standard'}</span>
                                        </div>
                                        <a class="cart-remove" href="#" onclick="removeFromCart(${cart.cartId}); event.preventDefault();">
                                            <i class="las la-times"></i>
                                        </a>
                                    </div>
                                </div>
                                </li>`;
            side_panel_cart_item_list.innerHTML += cartItem;
        });

        if (document.getElementById("side-panel-cart-sub-total")) {
            document.getElementById("side-panel-cart-sub-total").innerHTML = "Rs. " + new Intl.NumberFormat("en-US",
                {minimumFractionDigits: 2}).format(total);
        }
        if (document.getElementById("cart-count")) {
            document.getElementById("cart-count").innerHTML = totalQty;
        }
    }
}

async function de(cartQty) {
    const UpcartQty = cartQty+1;

    console.log("de cart" + UpcartQty);

    try {
        Notiflix.Loading.pulse("Wait...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });
        const response = await fetch(`api/carts/test123?UpcartQty=${UpcartQty}`);
        if (response.ok) {
            const data = await response.json();
            console.log(data);
            if (data.status) {
                Notiflix.Notify.success(data.message, {
                    position: 'center-top'
                });
                // await loadCartItems();
            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }
        } else {
            Notiflix.Notify.failure("Add to cart process failed!", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });

    } finally {
        Notiflix.Loading.remove();
    }
}

async function changeQuantity(cartId, action) {
    try {
        console.log(`Updating cart item ${cartId} with action: ${action}`);

        const response = await fetch(`api/carts/update-cart-qty?cartId=${cartId}&action=${action}`);

        if (response.status === 401) {
            window.location.href = '/unauthorized.html';
            return;
        }

        if (response.ok) {
            const data = await response.json();

            if (data.status) {

                const qtySpan = document.getElementById(`qty-val-${cartId}`);
                if (qtySpan) qtySpan.innerText = data.newQty;

                const totalSpan = document.getElementById(`total-price-${cartId}`);
                if (totalSpan) {
                    totalSpan.innerText = `$${Number(data.newTotalItemPrice).toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
                }

                console.log("Qty updated successfully! ");

                loadCartItems();

            } else {
                console.error("Server Error: " + data.message);
            }
        }
    } catch (e) {
        console.error("Error updating cart quantity:", e);
    }
}
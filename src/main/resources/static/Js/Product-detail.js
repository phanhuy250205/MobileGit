//Xem ảnh trong productdetail
function changeMainImage(element) {
    let  newImageSrc = element.getAttribute("data-src");
    document.getElementById("mainImage").src = newImageSrc;
}

//Thêm sản phẩm bên prodct-detail
function addToCart(button) {
    let sanPhamId = button.getAttribute("data-id");

    if (!sanPhamId) {
        alert("❌ Lỗi: Không tìm thấy sản phẩm!");
        return;
    }

    fetch('/cart/add?sanPhamId=' + sanPhamId, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.status === "success") {
                // ✅ Cập nhật số lượng sản phẩm trong giỏ hàng
                let cartBadge = document.getElementById('cart-badge');
                if (cartBadge) {
                    cartBadge.textContent = data.cartCount;
                }
                alert("✅ Đã thêm vào giỏ hàng!");
            } else {
                alert("❌ " + data.message);
            }
        })
        .catch(error => console.error("❌ Lỗi:", error));
}
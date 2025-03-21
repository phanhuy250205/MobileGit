// Thêm sản phẩm vào giỏ hàng từ trang sản phẩm
function addToCart(button) {
    let form = button.closest("form");
    let sanPhamId = form.querySelector("input[name='sanPhamId']").value;

    // Kiểm tra nếu nút bị vô hiệu hóa (sản phẩm hết hàng)
    if (button.disabled) {
        alert("❌ Sản phẩm này đã hết hàng, không thể thêm vào giỏ.");
        return;
    }

    fetch('/cart/add?sanPhamId=' + sanPhamId, {
        method: 'POST'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Lỗi khi gửi request!");
            }
            return response.json();
        })
        .then(data => {
            console.log("Phản hồi từ server:", data);

            if (data.status === "success") {
                let cartBadge = document.getElementById('cart-badge');
                if (cartBadge) {
                    cartBadge.textContent = data.cartCount; // ✅ Cập nhật số lượng giỏ hàng
                }
                alert("✅ Đã thêm vào giỏ hàng!");
            } else {
                alert("❌ Lỗi: " + data.message); // Hiển thị lỗi từ server (VD: sản phẩm hết hàng)
            }
        })
        .catch(error => {
            console.error('Lỗi:', error);
            alert("❌ Đã xảy ra lỗi khi thêm vào giỏ! Vui lòng thử lại.");
        });
}

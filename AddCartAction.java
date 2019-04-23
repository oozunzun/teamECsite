package com.internousdev.jupiter.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.jupiter.dao.CartInfoDAO;
import com.internousdev.jupiter.dto.CartInfoDTO;
import com.internousdev.jupiter.dto.ProductInfoDTO;
import com.opensymphony.xwork2.ActionSupport;

public class AddCartAction extends ActionSupport implements SessionAware {
	private ArrayList<CartInfoDTO> cartInfoDTOList;
	private CartInfoDAO cartInfoDAO = new CartInfoDAO();
	private ProductInfoDTO addCartList = new ProductInfoDTO();

	private Map<String, Object> session;
	private String user;
	private int productCount;
	private int totalPrice = 0;
	private int productId;

	public String execute() throws SQLException {
		if (session.isEmpty()) {
			return "sessionTimeout";
		}

		// カートに入れる商品を取得
		addCartList = (ProductInfoDTO) session.get("addCartList");
		productId = addCartList.getProductId();

		// ログイン状態を取得
		if (session.containsKey("userId")) {
			user = session.get("userId").toString();
		} else {
			user = session.get("tempUserId").toString();
		}

		if (cartInfoDAO.isExistsInCartItem(user, productId)) {

			// 同じ商品が既にカートに入っている場合
			// カートの中の商品個数を更新
			cartInfoDAO.updateProductCount(user, productId, productCount);
		} else {

			// 新しい商品の場合
			// カートに商品を追加
			cartInfoDAO.addCartInfo(user, productId, productCount, addCartList.getPrice());
		}

		// カートの中身を表示
		cartInfoDTOList = cartInfoDAO.getCartInfoList(user);
		for (CartInfoDTO cartList : cartInfoDTOList) {
			totalPrice += cartList.getSubtotal();
		}

		return SUCCESS;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}

	public ArrayList<CartInfoDTO> getCartInfoDTOList() {
		return cartInfoDTOList;
	}

	public void setCartInfoDTOList(ArrayList<CartInfoDTO> cartInfoDTOList) {
		this.cartInfoDTOList = cartInfoDTOList;
	}

	public int getProductCount() {
		return productCount;
	}

	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}

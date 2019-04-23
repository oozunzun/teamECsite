package com.internousdev.jupiter.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.jupiter.dao.CartInfoDAO;
import com.internousdev.jupiter.dto.CartInfoDTO;
import com.opensymphony.xwork2.ActionSupport;

public class DeleteCartAction extends ActionSupport implements SessionAware {
	private CartInfoDAO cartInfoDAO = new CartInfoDAO();
	private ArrayList<CartInfoDTO> cartInfoDTOList = new ArrayList<CartInfoDTO>();

	private Map<String, Object> session;
	private String user;
	private int[] deleteId;
	private int totalPrice = 0;
	private int count = 0;

	public String execute() throws SQLException {
		// セッションの有無確認
		if (session.isEmpty()) {
			return "sessionTimeout";
		}

		// ログイン状態を取得
		if (session.containsKey("userId")) {
			user = session.get("userId").toString();
		} else {
			user = session.get("tempUserId").toString();
		}

		// チェックした商品を削除
		for (int deleteId : deleteId) {
			count += cartInfoDAO.deleteCartItem(deleteId, user);
		}

		// 削除に失敗したらエラー
		if (count == 0) {
			return "error";
		}

		// カートの中身を表示
		cartInfoDTOList = cartInfoDAO.getCartInfoList(user);

		// カートの合計金額を計算
		for (CartInfoDTO cartList : cartInfoDTOList) {
			totalPrice += cartList.getSubtotal();
		}

		return SUCCESS;
	}

	public ArrayList<CartInfoDTO> getCartInfoDTOList() {
		return cartInfoDTOList;
	}

	public void setCartInfoDTOList(ArrayList<CartInfoDTO> cartInfoDTOList) {
		this.cartInfoDTOList = cartInfoDTOList;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public int[] getDeleteId() {
		return deleteId;
	}

	public void setDeleteId(int[] deleteId) {
		this.deleteId = deleteId;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}
}

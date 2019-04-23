package com.internousdev.jupiter.action;

import java.util.ArrayList;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.jupiter.dao.CartInfoDAO;
import com.internousdev.jupiter.dto.CartInfoDTO;
import com.opensymphony.xwork2.ActionSupport;

public class CartAction extends ActionSupport implements SessionAware {
	private CartInfoDAO cartInfoDAO = new CartInfoDAO();
	private ArrayList<CartInfoDTO> cartInfoDTOList = new ArrayList<CartInfoDTO>();

	private Map<String, Object> session;
	private String user;
	private int totalPrice = 0;

	public String execute() {
		if (session.isEmpty()) {
			return "sessionTimeout";
		}

		if (session.containsKey("userId")) {
			user = session.get("userId").toString();
		} else {
			user = session.get("tempUserId").toString();
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

	public Map<String, Object> getSession() {
		return session;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}

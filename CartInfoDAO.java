package com.internousdev.jupiter.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.internousdev.jupiter.dto.CartInfoDTO;
import com.internousdev.jupiter.util.DBConnector;

public class CartInfoDAO {

	// カート情報を取得するメソッド
	public ArrayList<CartInfoDTO> getCartInfoList(String userId) {
		ArrayList<CartInfoDTO> cartInfoDTOList = new ArrayList<CartInfoDTO>();

		// SQLと接続
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();

		// SQL文を定義
		String sql = "SELECT ci.id, ci.user_id, ci.product_id, ci.product_count, ci.price, ci.regist_date, ci.update_date, pi.product_name, pi.product_name_kana, pi.image_file_path, pi.image_file_name, pi.release_date, pi.release_company, pi.status FROM cart_info ci LEFT JOIN product_info pi ON ci.product_id = pi.product_id WHERE ci.user_id = ? ORDER BY ci.update_date DESC, ci.regist_date DESC";

		try {
			// SQL文を実行
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				// 取得した値をDTOへ保管
				CartInfoDTO CartInfoDTO = new CartInfoDTO();
				CartInfoDTO.setId(rs.getInt("id"));
				CartInfoDTO.setUserId(rs.getString("user_id"));
				CartInfoDTO.setProductId(rs.getInt("product_id"));
				CartInfoDTO.setProductCount(rs.getInt("product_count"));
				CartInfoDTO.setPrice(rs.getInt("price"));
				CartInfoDTO.setRegistDate(rs.getString("regist_date"));
				CartInfoDTO.setUpdateDate(rs.getString("update_date"));
				CartInfoDTO.setProductName(rs.getString("product_name"));
				CartInfoDTO.setProductNameKana(rs.getString("product_name_kana"));
				CartInfoDTO.setImageFilePath(rs.getString("image_file_path"));
				CartInfoDTO.setImageFileName(rs.getString("image_file_name"));
				CartInfoDTO.setReleaseDate(rs.getDate("release_date"));
				CartInfoDTO.setReleaseCompany(rs.getString("release_company"));
				CartInfoDTO.setSubtotal(rs.getInt("price") * rs.getInt("product_count"));
				CartInfoDTO.setStatus(rs.getString("status"));

				// DTOで保管している値をリストに入れる
				cartInfoDTOList.add(CartInfoDTO);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return cartInfoDTOList;
	}

	// カートに商品を追加するメソッド
	public void addCartInfo(String userId, int productId, int productCount, int price) {
		// SQLと接続
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();

		// SQL文を定義
		String sql = "INSERT INTO cart_info(user_id,product_id,product_count,price,regist_date,update_date)VALUES(?,?,?,?,now(),now())";

		try {
			// SQL文を実行
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setInt(2, productId);
			ps.setInt(3, productCount);
			ps.setInt(4, price);
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// 商品個数を増やすメソッド
	public int updateProductCount(String userId, int productId, int productCount) {
		// SQLと接続
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		int count = 0;

		String sql = "UPDATE cart_info SET product_count = (product_count + ?), update_date = now() where user_id = ? AND product_id = ?";

		try {
			// SQL文を実行
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, productCount);
			ps.setString(2, userId);
			ps.setInt(3, productId);
			count = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	// 同じ商品があるか確認するメソッド
	public boolean isExistsInCartItem(String userId, int productId) {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		String sql = "SELECT COUNT(id) AS COUNT FROM cart_info WHERE user_id = ? AND product_id=?";

		boolean result = false;

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setInt(2, productId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				if (rs.getInt("COUNT") > 0) {
					result = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	// 仮ユーザー時のカート情報をログインしたユーザーに更新するメソッド
	public int linkToUserId(String userId, String tempUserId, int productId) {
		// SQLと接続
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		int count = 0;

		String sql = "UPDATE cart_info SET user_id=?, update_date = now() where user_id = ? AND product_id = ?";

		try {
			// SQL文を実行
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, tempUserId);
			ps.setInt(3, productId);
			count = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	// 選択したカートの商品を削除するメソッド
	public int deleteCartItem(int deleteId, String userId) {
		// SQLと接続
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		int count = 0;

		// SQL文を定義
		String sql = "DELETE FROM cart_info WHERE id = ? AND user_id = ?";

		try {
			// SQL文を実行
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, deleteId);
			ps.setString(2, userId);
			count = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	// カートの商品全てを削除するメソッド
	public int deleteCartAll(String userId) {
		// SQLと接続
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		int count = 0;

		// SQL文を定義
		String sql = "DELETE FROM cart_info WHERE user_id = ?";

		try {
			// SQL文を実行
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			count = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
}

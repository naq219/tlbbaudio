package bhmedia.dungdq.kiemhiepaudio.model;

public class FavouriteList {
	
	// private variables
	String truyenid, tentruyen, tacgia, sotap, anhdaidien;
	
	public FavouriteList() {
		
	}
	
	public FavouriteList(String truyenid, String tentruyen, String tacgia, String sotap, String anhdaidien) {
		this.truyenid = truyenid;
		this.tentruyen = tentruyen;
		this.tacgia = tacgia;
		this.sotap = sotap;
		this.anhdaidien = anhdaidien;
	}
	
	public FavouriteList(String tentruyen, String tacgia, String sotap, String anhdaidien) {
		this.tentruyen = tentruyen;
		this.tacgia = tacgia;
		this.sotap = sotap;
		this.anhdaidien = anhdaidien;
	}
	
	// lấy TruyenID
	public String getTruyenID() {
		return this.truyenid;
	}
	// truyền TruyenID
	public void setTruyenID(String truyenid) {
		this.truyenid = truyenid;
	}
	
	// lấy tentruyen
	public String getTenTruyen() {
		return this.tentruyen;
	}
	// truyền tentruyen
	public void setTenTruyen(String tentruyen) {
		this.tentruyen = tentruyen;
	}
	
	// lấy tacgia
	public String getTacGia() {
		return this.tacgia;
	}
	// truyền tacgia
	public void setTacGia(String tacgia) {
		this.tacgia = tacgia;
	}
	
	// lấy sotap
	public String getSoTap() {
		return this.sotap;
	}
	// truyền sotap
	public void setSoTap(String sotap) {
		this.sotap = sotap;
	}
	
	// lấy anhdaidien
	public String getAnhDaiDien() {
		return this.anhdaidien;
	}
	// truyền anhdaidien
	public void setAnhDaiDien(String anhdaidien) {
		this.anhdaidien = anhdaidien;
	}
}

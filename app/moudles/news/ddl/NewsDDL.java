package moudles.news.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 
 * @author auto
 * @createDate 2016-07-27 18:14:00
 **/
@Table(name = "news")
public class NewsDDL {
	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "title", type = DbType.Varchar)
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "intro", type = DbType.Varchar)
	private String intro;

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}
	
	@Column(name = "is_index_show", type = DbType.Int)
	private Integer isIndexShow;

	public Integer getIsIndexShow() {
		return isIndexShow;
	}

	public void setIsIndexShow(Integer isIndexShow) {
		this.isIndexShow = isIndexShow;
	}

	@Column(name = "detail_url", type = DbType.Varchar)
	private String detailUrl;

	public String getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}

	@Column(name = "img_url", type = DbType.Varchar)
	private String imgUrl;

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	@Column(name = "sort", type = DbType.Int)
	private Integer sort;

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	@Column(name = "status", type = DbType.Int)
	private Integer status;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "notice_id", type = DbType.Int)
	private Integer noticeId;

	public Integer getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(Integer noticeId) {
		this.noticeId = noticeId;
	}

	@Column(name = "type", type = DbType.Int)
	private Integer type;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "op_name", type = DbType.Varchar)
	private String opName;

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	@Column(name = "update_time", type = DbType.DateTime)
	private Long updateTime;

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "create_time", type = DbType.DateTime)
	private Long createTime;

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	@Column(name = "games", type = DbType.Varchar)
	private String games;

	public String getGames() {
		return games;
	}

	public void setGames(String games) {
		this.games = games;
	}

	public static NewsDDL newExample() {
		NewsDDL object = new NewsDDL();
		object.setId(null);
		object.setTitle(null);
		object.setIntro(null);
		object.setDetailUrl(null);
		object.setSort(null);
		object.setStatus(null);
		object.setType(null);
		object.setOpName(null);
		object.setUpdateTime(null);
		object.setCreateTime(null);
		return object;
	}
}

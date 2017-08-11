package cn.breaksky.rounds.publics.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DBNAME = "publicsrounds.db";
	private static final int DBVERSION = 2;

	public DBHelper(Context context) {
		super(context, DBNAME, null, DBVERSION);
	}

	/**
	 * 在没有卸载的情况下，在线更新了版本2.0
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {//新安装的软件从这里开始
		// 消息表
		db.execSQL("CREATE TABLE message (" + //
				"id integer primary key autoincrement, " + // 自增长ID
				"message varchar(500), " + // 文本消息内容
				"sendid varchar(20)," + // 发送人ID
				"reciveid varchar(20)," + // 接收人ID
				"serviceid integer," + // 服务器消息ID
				"sendtime number(20)," + // 发送时间
				"status char(1)," + // 状态
				"type char(1)," + // 类型
				"file varchar(255)," + // 文件路径
				"latitude number(20,10)," + // 纬度
				"longitude number(20,10)" + // 经度
				")");
		// 区域
		db.execSQL("CREATE TABLE region(" + //
				"name varchar(50)," + // 区域名称
				"code varchar(100)" + // 编码
				")");
		// 事件表
		db.execSQL("CREATE TABLE eventinfo(" + //
				"id integer primary key autoincrement," + // 自增长ID
				"longitude varchar(20)," + // 经度
				"latitude varchar(20)," + // 纬度
				"address varchar(255)," + // 事件地址
				"contact varchar(20)," + // 联系人
				"phone varchar(15)," + // 联系人电话
				"image varchar(900)," + // 事件图片
				"context_type char(1)," + // 描述类型:语音/文本/图片/视频
				"context varchar(255)," + // 描述文本内容
				"context_file varchar(255)," + // 描述文件路径
				"time number(20,0)," + // 时间 长整型
				"serviceid number(20,0)" + // 服务器事件ID
				")");
		// 日常汇报表
		db.execSQL("CREATE TABLE reportinfo(" + //
				"id integer primary key autoincrement," + // 自增长ID
				"longitude varchar(20)," + // 经度
				"latitude varchar(20)," + // 纬度
				"address varchar(255)," + // 事件地址
				"contact varchar(20)," + // 联系人
				"phone varchar(15)," + // 联系人电话
				"context varchar(255)," + // 描述文本内容
				"time number(20,0)," + // 时间 长整型
				"serviceid number(20,0)" + // 服务器事件ID
				")");


	};
    /**
     * 数据库升级
     */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 
		if(oldVersion < newVersion){//如果版本是1.0的，升级下面的内容或修改
			// 日常汇报表
			db.execSQL("CREATE TABLE reportinfo(" + //
					"id integer primary key autoincrement," + // 自增长ID
					"longitude varchar(20)," + // 经度
					"latitude varchar(20)," + // 纬度
					"address varchar(255)," + // 事件地址
					"contact varchar(20)," + // 联系人
					"phone varchar(15)," + // 联系人电话
					"context varchar(255)," + // 描述文本内容
					"time number(20,0)," + // 时间 长整型
					"serviceid number(20,0)" + // 服务器事件ID
					")");
		 }

	};

}

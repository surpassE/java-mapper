package com.sirding;

import com.sirding.service.MapperService;

/**
 * @Described	: 
 * @project		: com.sirding.Start
 * @author 		: zc.ding
 * @date 		: 2017年1月7日
 */
public class Start {

	public static void main(String[] args) {
		MapperService mapper = new MapperService();
		if(args.length == 0){
			System.out.println("请输入表名，多个表名顺序追加");
			return;
		}
		for(String tableName : args){
			mapper.load(tableName);
		}
		System.out.println("操作完成.");
	}
}


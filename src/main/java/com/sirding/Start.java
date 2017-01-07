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
		mapper.load("app_menu");
		System.out.println("操作完成.");
	}
}


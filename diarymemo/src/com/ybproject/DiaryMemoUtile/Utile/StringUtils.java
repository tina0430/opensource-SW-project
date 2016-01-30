/**
 * 파일명: StringUtils.java
 * 최종수정: 2012년 2월 7일
 * 수정자: 이용범(top6616@gmail.com)
 * 설명: AndroidUtils 와 연동되면 빌드번호를 처리( 테스트 중이며 불필요시 삭제 예정 )
 */
package com.ybproject.DiaryMemoUtile.Utile;

import java.util.Collection;
import java.util.Iterator;


public class StringUtils {
	public static String join(Collection<?> s, String delimiter) {
	      StringBuilder builder = new StringBuilder();
	      Iterator<?> iter = s.iterator();
	      while (iter.hasNext()) {
	         builder.append(iter.next());
	          if (iter.hasNext()) {
	              builder.append(delimiter);
	          }
	      }
	      return builder.toString();
	 }

}

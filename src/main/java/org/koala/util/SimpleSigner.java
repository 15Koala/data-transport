/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See accompanying LICENSE file.
 */
package org.koala.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Date;

/**
 * Signs strings and verifies signed strings using a SHA digest.
 */
public class SimpleSigner {

	public String computeSignature(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update("admaster54322".getBytes());
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }

        byte[] byteArray;
        StringBuffer hexValue = new StringBuffer();
		try {
			byteArray = str.getBytes("UTF-8");
	        byte[] md5Bytes = md5.digest(byteArray);
	        for (int i = 0; i < md5Bytes.length; i++) {
	            int val = ((int) md5Bytes[i]) & 0xff;
	            if (val < 16) {
	                hexValue.append("0");
	            }
	            hexValue.append(Integer.toHexString(val));
	        }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return hexValue.toString();

	}

	public static String getToken(String tableName) {
		SimpleSigner siger = new SimpleSigner();
		return siger.computeSignature(tableName)
				.substring(0, 10);
	}
	
	public static String getRamTableName() {
		SimpleSigner siger = new SimpleSigner();
		return siger.computeSignature(String.valueOf(new Date().getTime()))
				.substring(0, 2);
	}

	public static void main(String[] args) {
		System.out.println(getToken("Ac"));
		System.out.println(getRamTableName());
	}
}

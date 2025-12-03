package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
	private static final String FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
			return;
		}

		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)
		//処理内容2-1
		//listFilesを使用してfilesという配列に、
		//指定したパスに存在する全てのファイルの情報を格納します。
        File[] files = new File(args[0]).listFiles();

        //先にファイルの情報を格納するList(ArrayList)を宣言します。
        List<File> rcdFiles = new ArrayList<>();

        //filesの数だけ繰り返すことで、
        //指定したパスに存在する全てのファイルの数だけ繰り返されます。
        for(int i = 0; i < files.length ; i++) {
        	  //ファイル名の取得とmatchesを使用してファイル名が「数字8桁.rcd」か判定
        	  if(files[i].getName().matches("\\d{8}.rcd")) {
        		  //売上ファイルの条件に当てはまったものだけ、List(ArrayList)に追加
        		  rcdFiles.add(files[i]);
        	  }
        }

        //処理内容2-2
        //brを空にする
        BufferedReader br = null;

        //rcdfilesに複数の売上ファイルの情報を格納しているので、その数だけ繰り返す
        for(int i = 0; i < rcdFiles.size(); i++) {

        	//売上ファイルの要素を一つ取り出す
        	File items;
        	items = rcdFiles.get(i);

        	try {
        	//brに取り出した売上ファイル情報を入れる
            File file = items;
     		FileReader fr = new FileReader(file);
            br = new BufferedReader(fr);

            //先にファイルの情報を格納するList(ArrayList)を宣言します。
            List<String> numbers = new ArrayList<>();

            //売上ファイルの中身を一行ずつ読み込む
            //売上ファイルの中身を新しいListを作成して保持する
        	String sale;
        	while((sale = br.readLine()) != null) {
        		numbers.add(sale);
        	}

        	//売上ファイルから読み込んだ売上金額をMapに加算していくために型の変換を行う
            long fileSale = Long.parseLong(numbers.get(1));

            //読み込んだ売上金額を加算します
            Long saleAmount = branchSales.get(numbers.get(0)) + fileSale;

        	//加算した売上金額をMapに追加します
        	branchSales.put(numbers.get(0),saleAmount);

        	} catch(IOException e) {
    			System.out.println(UNKNOWN_ERROR);
    			return;
    		} finally {
    			// ファイルを開いている場合
    			if(br != null) {
    				try {
    					// ファイルを閉じる
    					br.close();
    				} catch(IOException e) {
    					System.out.println(UNKNOWN_ERROR);
    					return;
    				}
    			}
    		}
    	}



		// 支店別集計ファイル書き込み処理
		if(!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;
		}

	}

	/**
	 * 支店定義ファイル読み込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 読み込み可否
	 */
	private static boolean readFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		BufferedReader br = null;

		try {
			File file = new File(path, fileName);
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while((line = br.readLine()) != null) {
				//spritを使って「,」で分割すると、
				//items[0]には支店コード、items[1]には支店名が格納されます。
				String[] items = line.split(",");

				//Mapに追加する2つの情報をputの引数として指定します。
				//固定の整数を追加する際に、何も指定しないとint型で扱われる。
				//Longで固定値を追加する場合は、「0L」のように記載し、Longの整数であることを示す。
				branchNames.put(items[0],items[1]);
				branchSales.put(items[0],0L);
				System.out.println(line);
			}

		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(br != null) {
				try {
					// ファイルを閉じる
					br.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		// ※ここに書き込み処理を作成してください。(処理内容3-1)
		//コマンドライン引数で指定されたディレクトリに支店別集計ファイルを作成
		BufferedWriter bw = null;

		try {
		//ファイル作成
		File newFile = new File(path,fileName);
		(newFile).createNewFile();

        //ファイルに書き込む処理の準備
		FileWriter fw = new FileWriter(newFile);
		bw = new BufferedWriter(fw);

		//ファイルに書き込む情報をMapから全てのKeyを取得する
		for(String key : branchSales.keySet()) {


			//取得したデータをファイルに書き込む
			bw.write(key + "," + branchNames.get(key) + "," + branchSales.get(key));

			//改行
			bw.newLine();
		}

		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(bw != null) {
				try {
					// ファイルを閉じる
					bw.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

}

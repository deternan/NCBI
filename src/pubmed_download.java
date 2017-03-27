import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class pubmed_download 
{
	// Parameters
	private String SAVE_path = System.getProperty("user.dir");
	private String TAR_folder = "TAR";
	private String nxml_folder = "nxml";
	// PubMed Central (PMC) 
	private String remoteFile = "pub/pmc/oa_file_list.txt";
	private String PMC_file_list = "file_list.txt";
	private String login_id_pass = "anonymous";
	private String oa_file_list = "oa_file_list.txt";
	// FTP
	private FTPClient client_nxml;
	private OutputStream outStream_nxml;
	// Decompress_tar
	private String Decompress_str;
	// Copy nxml
	private String copy_str;
	
	public pubmed_download() throws Exception
	{
		Download_Filelist();
		Read_record_download();
		Decompress_tar_Delection();
		Copy_nxml();
		
		client_nxml.disconnect();		
	}
	
	private void Download_Filelist() throws IOException
	{
		FTPClient client = new FTPClient();
		OutputStream outStream;
		client.connect("ftp.ncbi.nlm.nih.gov");
		client.login(login_id_pass, login_id_pass);				
		outStream = new FileOutputStream(PMC_file_list);
		client.retrieveFile(remoteFile, outStream);
		client.disconnect();
	}
	
	private void Read_record_download() throws IOException
	{
		client_nxml = new FTPClient( );
		client_nxml.connect("ftp.ncbi.nlm.nih.gov");
		client_nxml.login(login_id_pass, login_id_pass);
		client_nxml.setFileType(FTP.BINARY_FILE_TYPE);
		String remoteFile;
		
		FileReader fr = new FileReader(SAVE_path+oa_file_list);
		BufferedReader bfr = new BufferedReader(fr);
		
		String Line;
		String[] temp;
		
		while((Line = bfr.readLine())!=null)
		{			
			temp = Line.split("\t");
			remoteFile = temp[0];
			Download_nxml(remoteFile);
		}
	}
	
	private void Download_nxml(String remoteFile) throws IOException
	{
		String temp;						
		String nxml_tar[] = remoteFile.split("/");		
							
		temp = "pub/pmc/".concat(remoteFile);
		outStream_nxml = new FileOutputStream(SAVE_path+"/"+TAR_folder+"/".concat(nxml_tar[3]));	
		client_nxml.retrieveFile(temp, outStream_nxml);		
	}
	
	private void Decompress_tar_Delection() throws Exception
	{
		File obj = new File(SAVE_path+"/"+TAR_folder+"/");
		String tar_list[] = obj.list();
		
		for(int i=0;i<tar_list.length;i++)
		{			
			{
				Decompress_str = "tar -xvf tar/"+tar_list[i]+" -C "+SAVE_path+"/"+TAR_folder+"/";					
				Process pl = Runtime.getRuntime().exec(Decompress_str);
	            BufferedReader p_in = new BufferedReader(new InputStreamReader(pl.getInputStream()));
	            
	            try {	            	
					pl.waitFor();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            p_in.close();
	            
	            //Delete Tar
	            Delete_TAR(tar_list[i]);
			}
		}
	}
	
	private void Delete_TAR(String tar) throws Exception
	{
		String delete_tar = "rm "+SAVE_path+"/"+TAR_folder+"/"+tar;
		Process pl = Runtime.getRuntime().exec(delete_tar);
        BufferedReader p_in = new BufferedReader(new InputStreamReader(pl.getInputStream()));
		
        try {
			pl.waitFor();			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        p_in.close();
	}
	
	private void Copy_nxml() throws Exception
	{		
		File obj = new File(SAVE_path+"/"+TAR_folder+"/");
		String dir_list[] = obj.list();
		
		for(int i=0;i<dir_list.length;i++)
		{
			File file2 = new File(SAVE_path+"/"+TAR_folder+"/"+dir_list[i]); 
			String articleList2[] = file2.list();
			
			for(int j=0;j<articleList2.length;j++)
			{
				if(articleList2[j].contains(".nxml")){	
					copy_str = "cp "+SAVE_path+"/"+TAR_folder+"/"+dir_list[i]+"/"+articleList2[j]+" "+SAVE_path+"/"+nxml_folder+"/"+dir_list[i]+".nxml";
				//System.out.println(copy_str);	
					Process pl = Runtime.getRuntime().exec(copy_str);
		            BufferedReader p_in = new BufferedReader(new InputStreamReader(pl.getInputStream()));
		            
		            try {
						pl.waitFor();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            p_in.close();
				}
			}
						
            // Delete Folder
            Delete_DIR(dir_list[i]);
		}
	}
	
	private void Delete_DIR(String dir) throws Exception
	{
		String delete_dir = "rm -rf "+SAVE_path+"/"+TAR_folder+"/"+dir;
		
		Process pl = Runtime.getRuntime().exec(delete_dir);
        BufferedReader p_in = new BufferedReader(new InputStreamReader(pl.getInputStream()));
		
        try {
			pl.waitFor();			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        p_in.close();
	}
	
	public static void main(String args[])
	{
		try {
			pubmed_download pubmed = new pubmed_download();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

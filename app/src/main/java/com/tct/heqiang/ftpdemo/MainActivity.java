package com.tct.heqiang.ftpdemo;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText serverUrlText;
    private EditText usernameText;
    private EditText passwordText;
    private Button cancel_button, login_button;

    private String serverUrl, username, password;
    private String SharedPref_Name = "data";
    private ScrollView sv;
    private ProgressDialog dialog;

    private String TAG = "mylog";

    private final int START_TO_LOGIN = 1;
    private final int LOGIN_SUCCESS = 2;
    private final int LOGIN_FAIL = 3;
    private final int ENTER_CHILD = 4;

    private FTPClient client;
    private LinearLayout mLoginPart;
    private ArrayList<String> rootFileNames,childFileList;
    private ListView mList;

    private FTPFile[] ftpRootFiles = null;

    private FTPFile file = null;
    private SimpleAdapter mAdapter = null;

    private String currentPath = "/";

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case START_TO_LOGIN:
                    dialog = new ProgressDialog(MainActivity.this);
                    dialog.setTitle("Notice");
                    dialog.setMessage("Loading...");
                    dialog.show();
                    break;
                case LOGIN_SUCCESS:
                    dialog.dismiss();
                    mLoginPart.setVisibility(View.GONE);
                    mList.setVisibility(View.VISIBLE);

                    try {
                        BufferedReader reader = null;
                        String line = null;

                        setAdapter(rootFileNames);
                        mList.setAdapter(mAdapter);

                        //FileAdapter adapter = new FileAdapter(MainActivity.this, rootFileNames,);
                    }catch (Exception e){
                        Log.d(TAG, "exception ...");
                    }
                    break;
                case ENTER_CHILD:
                    mList.setAdapter(mAdapter);
                    break;
                case LOGIN_FAIL:
                    Toast.makeText(MainActivity.this,"Connect fali....",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    break;
                default:
                    dialog.dismiss();
                    break;
            }
        }
    };

    private void setAdapter( ArrayList<String> arrayList){
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < arrayList.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            //map.put("image", R.mipmap.ic_launcher);
            map.put("image", imageId(arrayList.get(i)));
            map.put("title", arrayList.get(i));
            data.add(map);
        }

        mAdapter = new SimpleAdapter(MainActivity.this, data, R.layout.item_menu,
                new String[]{"image", "title"}, new int[]{R.id.item_image, R.id.item_title});
    }

    public int imageId(String name){
        int id = 0;
        String fileEnds = name.substring(name.lastIndexOf(".") + 1,
                name.length()).toLowerCase();
        if(fileEnds.equals("m4a")||fileEnds.equals("mp3")||fileEnds.equals("mid")||fileEnds.equals("xmf")||fileEnds.equals("ogg")||fileEnds.equals("wav")){
            id = R.drawable.audio;
        }else if(fileEnds.equals("3gp")||fileEnds.equals("mp4")){
            id = R.drawable.video;
        }else if(fileEnds.equals("jpg")||fileEnds.equals("gif")||fileEnds.equals("png")||fileEnds.equals("jpeg")||fileEnds.equals("bmp")){
            id = R.drawable.image;
        }else if(fileEnds.equals("apk")){
            id = R.drawable.apk;
        }else if(fileEnds.equals("txt")){
            id = R.drawable.txt;
        }else if(fileEnds.equals("zip")||fileEnds.equals("rar")){
            id = R.drawable.zip_icon;
        }else if(fileEnds.equals("html")||fileEnds.equals("htm")||fileEnds.equals("mht")){
            id = R.drawable.web_browser;
        }else {
            id = R.drawable.others;
        }

        return id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
        setListener();

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.d(TAG, "position is: " + position);

                /*if(currentPath.equals("/")){
                    if(ftpRootFiles[position].isDirectory()){
                        file = ftpRootFiles[position];
                        try{
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    try{
                                        client.setControlEncoding("GBK");
                                        //File mFile = new File((File)ftpRootFiles[0],"myfile");
                                        FTPFile[] rootFTPFiles = client.listFiles();
                                        FTPFile parent = rootFTPFiles[position];
                                        Log.d(TAG," parent.getName() is :" + parent.getName());
                                        //String parentName = new String(parent.getName().getBytes(), "GBK");
                                        FTPFile[] childFTPFiles = client.listFiles( "/" +parent.getName() + "/");

                                        Log.d(TAG,"第" + "行，共有" + childFTPFiles.length + "个文件");

                                        childFileList = new ArrayList<String>();
                                        for(int i = 0; i < childFTPFiles.length; i++){
                                            String name = childFTPFiles[i].getName();
                                            childFileList.add(name);
                                            Log.d(TAG,"childFileList are :" + name);
                                        }

                                        setAdapter(childFileList);

                                        currentPath = currentPath + parent.getName() + "/";
                                        sendMsg(ENTER_CHILD);

                                    }catch (IOException e){

                                    }


                                }
                            }).start();

                            //client.logout();
                            //client.disconnect();
                        }catch (Exception e){

                        }
                    }

                }else{
                    Log.d(TAG, "before click currentPath is :" + currentPath);
                    listChildFiles(currentPath,position);
                }*/

                Log.d(TAG, "before click currentPath is :" + currentPath);
                listChildFiles(currentPath, position);
            }
        });
    }

    public String toFormatEncoding(String name){
        try{
            return new String(name.getBytes("GBK"),"iso-8859-1");
        }catch (UnsupportedEncodingException e){
            Log.d(TAG,"UnsupportedEncodingException...");
        }
        return "";
    }

    public void listChildFiles(final String filePath,final int position){

        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = currentPath;
                try{
                    FTPFile[] parentFTPFiles = client.listFiles(filePath);
                    FTPFile parentFTPFile = parentFTPFiles[position];
                    if(parentFTPFile.isDirectory()) {
                        currentPath = path + toFormatEncoding(parentFTPFile.getName())+ "/";
                        Log.d(TAG, "after click currentPath is :" + currentPath);
                        FTPFile[] childFiles = client.listFiles(currentPath);

                        Log.d(TAG, parentFTPFile.getName() + "下共有" + childFiles.length + "个文件");
                        if(childFiles.length != 0){
                            childFileList = new ArrayList<String>();
                            for (int i = 0; i < childFiles.length; i++) {
                                String name = childFiles[i].getName();
                                childFileList.add(name);
                                Log.d(TAG, "childFileList are :" + name);
                            }
                            setAdapter(childFileList);
                            sendMsg(ENTER_CHILD);
                        }else{
                            currentPath = path;
                        }
                    }else{
                        currentPath = path;
                        String parentFileName = parentFTPFile.getName();
                        String fileEnds = parentFileName.substring(parentFileName.lastIndexOf(".") + 1,
                                parentFileName.length()).toLowerCase();
                        if("3gp".equals(fileEnds) || "mp4".equals(fileEnds)){
                            Log.d(TAG,"this is a video!");

                            Intent i = new Intent();
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.setAction(Intent.ACTION_VIEW);
                            i.setDataAndType(Uri.parse("ftp://" + serverUrl + currentPath),"video/*");
                            startActivity(i);
                        }
                    }
                }catch (Exception e){
                    Log.d(TAG,"report a exception!");
                }
            }
        }).start();
    }



    public void findView() {
        serverUrlText = (EditText) findViewById(R.id.serverUrl);
        usernameText = (EditText) findViewById(R.id.username);
        passwordText = (EditText) findViewById(R.id.password);
        cancel_button = (Button) findViewById(R.id.cancelbtn);
        login_button = (Button) findViewById(R.id.loginbtn);

        mLoginPart = (LinearLayout) findViewById(R.id.loginPart);

        mList = (ListView) findViewById(android.R.id.list);
    }

    public void setListener() {
        cancel_button.setOnClickListener(this);
        login_button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelbtn:
                break;
            case R.id.loginbtn:
                login();
                break;
            default:
                break;
        }

    }

    public void login() {
        serverUrl = serverUrlText.getText().toString().trim();
        username = usernameText.getText().toString().trim();
        password = passwordText.getText().toString().trim();
        if (!"".equals(serverUrl) && !"".equals(username) && !"".equals(password)) {
            sendMsg(START_TO_LOGIN);

            client = new FTPClient();
            //client.setControlEncoding("GBK");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        Log.d(TAG, "start to login..");

                        client.connect(serverUrl, 21);
                        client.setControlEncoding("GBK");//encoding避免乱码,注意不能采用“UTF-8”

                        client.enterLocalPassiveMode();
                        client.login(username, password);

                        int relpy = client.getReplyCode();
                        Log.d(TAG, "login is : " + client.getReplyCode());
                        if (FTPReply.isPositiveCompletion(relpy)) {

                            client.setFileType(FTP.BINARY_FILE_TYPE);

                            Log.d(TAG, "login is success!");

                            ftpRootFiles = client.listFiles();

                            //ftpRootFiles =  client.listFiles();

                            Log.d(TAG,"ftpRootFiles size are :" + ftpRootFiles.length);

                            rootFileNames = new ArrayList<String>();
                            for(int i = 0; i < ftpRootFiles.length; i++){
                                String name = ftpRootFiles[i].getName();
                                rootFileNames.add(name);
                                Log.d(TAG,"rootfiles are :" + name);
                            }

                            currentPath = "/";
                            sendMsg(LOGIN_SUCCESS);
                        } else {
                            sendMsg(LOGIN_FAIL);
                        }

                        //client.logout();
                        //client.disconnect();
                        //Log.d(TAG, "client is off!");
                    } catch (UnknownHostException e1) {

                    } catch (SocketException e2) {

                    } catch (IOException e3) {

                    }
                }
            }).start();

        } else {
            Toast.makeText(MainActivity.this, "请填写完整相关的信息", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendMsg(int n) {
        Message msg = new Message();
        msg.what = n;
        mHandler.sendMessage(msg);
    }




}

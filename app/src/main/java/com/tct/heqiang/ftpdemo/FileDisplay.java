package com.tct.heqiang.ftpdemo;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FileDisplay extends ListActivity {
    private ListView listView;

    private String mRootPath = java.io.File.separator;
    public static String mCurrentFilePath = "";
    private List<String> mFileName = null;
    private List<String> mFilePaths = null;
    private static int menuPosition = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_display);

        listView = (ListView) findViewById(android.R.id.list);

        FTPFile[] ftpFiles= (FTPFile[])getIntent().getSerializableExtra("files");
        FTPFile ftpFile = ftpFiles[0];


    }

    private void initFileListInfo(File[] mFiles){
        //isAddBackUp = false;
        mCurrentFilePath = mFiles[0].getPath();

        //mPath.setText(filePath);
        mFileName = new ArrayList<String>();
        mFilePaths = new ArrayList<String>();
        //File mFile = new File(filePath);

        //File[] mFiles = mFile.listFiles();
        /*if(menuPosition == 1&&!mCurrentFilePath.equals(mRootPath)){
            initAddBackUp(filePath,mRootPath);
        }else if(menuPosition == 2&&!mCurrentFilePath.equals(mSDCard)){
            initAddBackUp(filePath,mSDCard);
        }*/


        for(File mCurrentFile:mFiles){
            mFileName.add(mCurrentFile.getName());
            mFilePaths.add(mCurrentFile.getPath());
        }

        setListAdapter(new FileAdapter(FileDisplay.this, mFileName, mFilePaths));
    }

    public class FileAdapter extends BaseAdapter {
        private Bitmap mBackRoot;
        private Bitmap mBackUp;
        private Bitmap mImage;
        private Bitmap mAudio;
        private Bitmap mRar;
        private Bitmap mVideo;
        private Bitmap mFolder;
        private Bitmap mApk;
        private Bitmap mOthers;
        private Bitmap mTxt;
        private Bitmap mWeb;

        private Context mContext;

        private List<String> mFileNameList;

        private List<String> mFilePathList;

        public FileAdapter(Context context,List<String> fileName,List<String> filePath){
            mContext = context;
            mFileNameList = fileName;
            mFilePathList = filePath;

            mBackRoot = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.back_to_root);
            //���ص���һ��Ŀ¼
            mBackUp = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.back_to_up);
            //ͼƬ�ļ���Ӧ��icon
            mImage = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.image);
            //��Ƶ�ļ���Ӧ��icon
            mAudio = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.audio);
            //��Ƶ�ļ���Ӧ��icon
            mVideo = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.video);
            //��ִ���ļ���Ӧ��icon
            mApk = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.apk);
            //�ı��ĵ���Ӧ��icon
            mTxt = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.txt);
            //���������ļ���Ӧ��icon
            mOthers = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.others);
            //�ļ��ж�Ӧ��icon
            mFolder = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.folder);
            //zip�ļ���Ӧ��icon
            mRar = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.zip_icon);
            //��ҳ�ļ���Ӧ��icon
            mWeb = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.web_browser);
        }

        public int getCount() {
            return mFilePathList.size();
        }

        public Object getItem(int position) {
            return mFileNameList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup viewgroup) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater mLI = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = mLI.inflate(R.layout.list_child, null);

                viewHolder.mIV = (ImageView)convertView.findViewById(R.id.image_list_childs);
                viewHolder.mTV = (TextView)convertView.findViewById(R.id.text_list_childs);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            File mFile = new File(mFilePathList.get(position).toString());

            if(mFileNameList.get(position).toString().equals("BacktoRoot")){
                viewHolder.mIV.setImageBitmap(mBackRoot);
                viewHolder.mTV.setText("返回根目录");
            }else if(mFileNameList.get(position).toString().equals("BacktoUp")){
                viewHolder.mIV.setImageBitmap(mBackUp);
                viewHolder.mTV.setText("返回上一级目录");
            }else if(mFileNameList.get(position).toString().equals("BacktoSearchBefore")){
                //��ӷ�������֮ǰĿ¼�İ�ť
                viewHolder.mIV.setImageBitmap(mBackRoot);
                viewHolder.mTV.setText("返回根目录");
            }else{
                String fileName = mFile.getName();
                viewHolder.mTV.setText(fileName);
                if(mFile.isDirectory()){
                    viewHolder.mIV.setImageBitmap(mFolder);
                }else{
                    String fileEnds = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length()).toLowerCase();//ȡ���ļ���׺����ת��Сд
                    if(fileEnds.equals("m4a")||fileEnds.equals("mp3")||fileEnds.equals("mid")||fileEnds.equals("xmf")||fileEnds.equals("ogg")||fileEnds.equals("wav")){
                        viewHolder.mIV.setImageBitmap(mVideo);
                    }else if(fileEnds.equals("3gp")||fileEnds.equals("mp4")){
                        viewHolder.mIV.setImageBitmap(mAudio);
                    }else if(fileEnds.equals("jpg")||fileEnds.equals("gif")||fileEnds.equals("png")||fileEnds.equals("jpeg")||fileEnds.equals("bmp")){
                        viewHolder.mIV.setImageBitmap(mImage);
                    }else if(fileEnds.equals("apk")){
                        viewHolder.mIV.setImageBitmap(mApk);
                    }else if(fileEnds.equals("txt")){
                        viewHolder.mIV.setImageBitmap(mTxt);
                    }else if(fileEnds.equals("zip")||fileEnds.equals("rar")){
                        viewHolder.mIV.setImageBitmap(mRar);
                    }else if(fileEnds.equals("html")||fileEnds.equals("htm")||fileEnds.equals("mht")){
                        viewHolder.mIV.setImageBitmap(mWeb);
                    }else {
                        viewHolder.mIV.setImageBitmap(mOthers);
                    }
                }
            }
            return convertView;
        }

        class ViewHolder {
            ImageView mIV;
            TextView mTV;
        }
    }

    public SimpleAdapter myAdapter(String[] fileNameList, int imageId){
        ArrayList<HashMap<String,Object>> data = new ArrayList<HashMap<String,Object>>();
        for(int i = 0;i < fileNameList.length; i++){
            HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("image",imageId);
            map.put("title",fileNameList[i]);
            data.add(map);
        }

        SimpleAdapter mAdapter = new SimpleAdapter(this,data,R.layout.item_menu,
                new String[]{"image","title"},new int[]{R.id.item_image,R.id.item_title});
        return mAdapter;
    }
}

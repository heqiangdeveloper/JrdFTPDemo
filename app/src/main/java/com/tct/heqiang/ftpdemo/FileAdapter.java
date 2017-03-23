package com.tct.heqiang.ftpdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by user on 3/23/17.
 */
public class FileAdapter extends BaseAdapter{

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
    private FTPClient mClient;

    public FileAdapter(Context context,List<String> fileName,List<String> filePath, FTPClient client){
        mContext = context;
        mFileNameList = fileName;
        mFilePathList = filePath;
        mClient = client;

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
        //File mFile = new File(mFilePathList.get(position).toString());

        FTPFile[] mFiles = null;
        FTPFile mFile = null;
        try {
            mFiles = mClient.listFiles(mFilePathList.get(position));
            mFile = mFiles[position];
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(mFileNameList.get(position).toString().equals("BacktoRoot")){
            //��ӷ��ظ�Ŀ¼�İ�ť
            viewHolder.mIV.setImageBitmap(mBackRoot);
            viewHolder.mTV.setText("���ظ�Ŀ¼");
        }else if(mFileNameList.get(position).toString().equals("BacktoUp")){
            //��ӷ�����һ���˵��İ�ť
            viewHolder.mIV.setImageBitmap(mBackUp);
            viewHolder.mTV.setText("������һ��");
        }else if(mFileNameList.get(position).toString().equals("BacktoSearchBefore")){
            //��ӷ�������֮ǰĿ¼�İ�ť
            viewHolder.mIV.setImageBitmap(mBackRoot);
            viewHolder.mTV.setText("��������֮ǰĿ¼");
        }else{
            String fileName = mFile.getName();
            viewHolder.mTV.setText(fileName);
            if(mFile.isDirectory()){
                viewHolder.mIV.setImageBitmap(mFolder);
            }else{
                String fileEnds = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length()).toLowerCase();//ȡ���ļ���׺����ת��Сд
                if(fileEnds.equals("m4a")||fileEnds.equals("mp3")||fileEnds.equals("mid")||fileEnds.equals("xmf")||fileEnds.equals("ogg")||fileEnds.equals("wav")){
                    viewHolder.mIV.setImageBitmap(mAudio);
                }else if(fileEnds.equals("3gp")||fileEnds.equals("mp4")){
                    viewHolder.mIV.setImageBitmap(mVideo);
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

using System;
using System.Text;
using System.Security.Cryptography;  

namespace Kingdee.K3.SCM.PDABinding.Code
{
    class SoftReg
    {
        //private static int len = 20;
        public int[] intCode = new int[127];    //存储密钥
        //public char[] charCode = new char[len];  //存储ASCII码
        //public int[] intNumber = new int[len];   //存储ASCII码值

        private int len { get; set; }
        private string SN { get; set; }
        private string MIEI { get; set; }

        public SoftReg(string sn, string miei)
        {
            this.SN = sn;
            this.MIEI = miei;
            this.len = sn.Length + miei.Length;
        }

        //初始化密钥
        public void SetIntCode()
        {
            for (int i = 1; i < intCode.Length; i++)
            {
                intCode[i] = i % 9;
            }
        }


        ///<summary>
        /// 生成注册码
        ///</summary>
        ///<returns></returns>
        private string GetRNum()
        {
            char[] charCode = new char[len];  //存储ASCII码
            int[] intNumber = new int[len];   //存储ASCII码值

            SetIntCode();
            string strMNum = SN + MIEI;
            strMNum = strMNum.Substring(0, len-1);

            for (int i = 1; i < charCode.Length; i++)   //存储机器码
            {
                charCode[i] = Convert.ToChar(strMNum.Substring(i - 1, 1));
            }
            for (int j = 1; j < intNumber.Length; j++)  //改变ASCII码值
            {
                intNumber[j] = Convert.ToInt32(charCode[j]) + intCode[Convert.ToInt32(charCode[j])];
            }
            string strAsciiName = "";   //注册码
            for (int k = 1; k < intNumber.Length; k++)  //生成注册码
            {

                if ((intNumber[k] >= 48 && intNumber[k] <= 57) || (intNumber[k] >= 65 && intNumber[k]
                    <= 90) || (intNumber[k] >= 97 && intNumber[k] <= 122))  //判断如果在0-9、A-Z、a-z之间
                {
                    strAsciiName += Convert.ToChar(intNumber[k]).ToString();
                }
                else if (intNumber[k] > 122)  //判断如果大于z
                {
                    strAsciiName += Convert.ToChar(intNumber[k] - 10).ToString();
                }
                else
                {
                    strAsciiName += Convert.ToChar(intNumber[k] - 9).ToString();
                }
            }
            return strAsciiName;
        }

        public string CreateLicenseFile()
        {
            string secCode = UserMd5(GetRNum());

            //得到数据进行加密处理

            return secCode;
        }

        /// <summary>
        /// MD5 32
        /// </summary>
        /// <param name="str"></param>
        /// <returns></returns>
        private string UserMd5(string str)
        {
            string cl = str;
            string pwd = "";
            MD5 md5 = MD5.Create();//实例化一个md5对像
            // 加密后是一个字节类型的数组，这里要注意编码UTF8/Unicode等的选择　
            byte[] s = md5.ComputeHash(Encoding.UTF8.GetBytes(cl));
            // 通过使用循环，将字节类型的数组转换为字符串，此字符串是常规字符格式化所得
            for (int i = 0; i < s.Length; i++)
            {
                // 将得到的字符串使用十六进制类型格式。格式后的字符是小写的字母，如果使用大写（X）则格式后的字符是大写字符
                pwd = pwd + s[i].ToString("X");

            }
            return pwd;
        }
    }
}

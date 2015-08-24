using System;
using System.IO;
using System.Data;
using System.Linq;
using System.Text;
using System.Drawing;
using System.Windows.Forms;
using System.ComponentModel;
using System.Threading.Tasks;
using System.Collections.Generic;

namespace Kingdee.K3.SCM.PDABinding
{
    public partial class License : Form
    {
        public License()
        {
            InitializeComponent();
        }

        /// <summary>
        /// 生成 license 文件，并提示用户选择保存的路径
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void generate_Click(object sender, EventArgs e)
        {
            if (string.IsNullOrEmpty(txt_sn.Text) || string.IsNullOrEmpty(txt_miei.Text))
            {
                MessageBox.Show("SN 号和 MIEI 号不能为空");
            }
            else
            {
                try
                {
                    SaveFileDialog dialog = new SaveFileDialog();
                    //dialog.Filter = @"License|*.license";
                    dialog.FilterIndex = 0;
                    //dialog.FileName = txt_sn.Text;
                    dialog.FileName = Kingdee.K3.SCM.PDABinding.Code.StringUtils.replaceUrlWithPlus(txt_sn.Text + ".license");

                    if (dialog.ShowDialog() == DialogResult.OK)
                    {
                        StringBuilder strMessage = new StringBuilder();

                        Code.SoftReg register = new Code.SoftReg();

                        strMessage.Append(register.CreateLicenseFile(txt_sn.Text, txt_miei.Text));
                        if (!dialog.CheckPathExists)
                        {
                            Directory.CreateDirectory(dialog.FileName);
                        }

                        File.Delete(dialog.FileName);
                        File.AppendAllText(dialog.FileName, strMessage.ToString());
                        MessageBox.Show("保存成功");
                        //打开保存的文件路径
                    }
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.Message);
                }
            }
        }

        private void txt_sn_TextChanged(object sender, EventArgs e)
        {
            txt_sn.Text = ChangeToUpper(txt_sn.Text);
            if (txt_sn.Text.Length > 0)
            {
                txt_sn.SelectionStart = txt_sn.Text.Length;
            }
        }

        private void txt_miei_TextChanged(object sender, EventArgs e)
        {
            txt_miei.Text = ChangeToUpper(txt_miei.Text);
            if (txt_miei.Text.Length > 0)
            {
                txt_miei.SelectionStart = txt_miei.Text.Length;
            }
        }

        private string ChangeToUpper(string str)
        {
            string finalStr = "";
            if (str.Length > 0)
            {
                // 將 TextBox1.Text 的中文字刪除
                //for (int i = str.Length - 1; i >= 0; i--)
                //{
                    //if (!(System.Text.RegularExpressions.Regex.IsMatch(str.Substring(i, 1), @"^[A-Za-z0-9]+$")))
                    //{
                    //    str = str.Remove(i, 1);
                        
                    //}


                //}
                str = str.Replace(" ", "");

                char[] arr = str.ToCharArray();
                for (int count = arr.Length - 1; count >= 0; count--)
                {
                    finalStr += arr[count];
                    if (count % 4 == 0 && count != 0)
                    {
                        finalStr += " ";
                    }
                }
            }


            return finalStr.ToUpper();
        }
    }
}

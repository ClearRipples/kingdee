namespace Kingdee.K3.SCM.PDABinding
{
    partial class License
    {
        /// <summary>
        /// 必需的设计器变量。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// 清理所有正在使用的资源。
        /// </summary>
        /// <param name="disposing">如果应释放托管资源，为 true；否则为 false。</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows 窗体设计器生成的代码

        /// <summary>
        /// 设计器支持所需的方法 - 不要
        /// 使用代码编辑器修改此方法的内容。
        /// </summary>
        private void InitializeComponent()
        {
            this.sn = new System.Windows.Forms.Label();
            this.miei = new System.Windows.Forms.Label();
            this.generate = new System.Windows.Forms.Button();
            this.txt_sn = new System.Windows.Forms.TextBox();
            this.txt_miei = new System.Windows.Forms.TextBox();
            this.license_save = new System.Windows.Forms.SaveFileDialog();
            this.license_open = new System.Windows.Forms.OpenFileDialog();
            this.SuspendLayout();
            // 
            // sn
            // 
            this.sn.AutoSize = true;
            this.sn.Location = new System.Drawing.Point(58, 40);
            this.sn.Name = "sn";
            this.sn.Size = new System.Drawing.Size(35, 12);
            this.sn.TabIndex = 0;
            this.sn.Text = "SN 号";
            // 
            // miei
            // 
            this.miei.AutoSize = true;
            this.miei.Location = new System.Drawing.Point(46, 112);
            this.miei.Name = "miei";
            this.miei.Size = new System.Drawing.Size(47, 12);
            this.miei.TabIndex = 1;
            this.miei.Text = "MIEI 号";
            // 
            // generate
            // 
            this.generate.Location = new System.Drawing.Point(145, 205);
            this.generate.Name = "generate";
            this.generate.Size = new System.Drawing.Size(245, 23);
            this.generate.TabIndex = 2;
            this.generate.Text = "生成 License";
            this.generate.UseVisualStyleBackColor = true;
            this.generate.Click += new System.EventHandler(this.generate_Click);
            // 
            // txt_sn
            // 
            this.txt_sn.Location = new System.Drawing.Point(122, 37);
            this.txt_sn.Name = "txt_sn";
            this.txt_sn.Size = new System.Drawing.Size(323, 21);
            this.txt_sn.TabIndex = 3;
            this.txt_sn.KeyUp += new System.Windows.Forms.KeyEventHandler(this.txt_sn_KeyUp);
            // 
            // txt_miei
            // 
            this.txt_miei.Location = new System.Drawing.Point(122, 109);
            this.txt_miei.Name = "txt_miei";
            this.txt_miei.Size = new System.Drawing.Size(323, 21);
            this.txt_miei.TabIndex = 4;
            this.txt_miei.KeyUp += new System.Windows.Forms.KeyEventHandler(this.txt_miei_KeyUp);
            // 
            // license_open
            // 
            this.license_open.FileName = "license_open";
            // 
            // License
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(543, 301);
            this.Controls.Add(this.txt_miei);
            this.Controls.Add(this.txt_sn);
            this.Controls.Add(this.generate);
            this.Controls.Add(this.miei);
            this.Controls.Add(this.sn);
            this.Name = "License";
            this.Text = "License 生成器";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label sn;
        private System.Windows.Forms.Label miei;
        private System.Windows.Forms.Button generate;
        private System.Windows.Forms.TextBox txt_sn;
        private System.Windows.Forms.TextBox txt_miei;
        private System.Windows.Forms.SaveFileDialog license_save;
        private System.Windows.Forms.OpenFileDialog license_open;
    }
}


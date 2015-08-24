using System;
using System.Text.RegularExpressions;

namespace Kingdee.K3.SCM.PDABinding.Code
{
    /*
     * author: hongbo_liang @ kingdee.com
     * date: 2015-08-19
     * description: 处理特殊字符
     */
    public class StringUtils
    {
        //1. 处理特殊字符
        //2. 去除后缀名带来的文件浏览器的视图凌乱(特别是图片更需要如此类似处理，否则有的手机打开图库，全是我们的缓存图片)
        public static string replaceUrlWithPlus(string path)
        {
            path = Regex.Replace(path, @"http://(.)*?/", @"", RegexOptions.IgnoreCase);
            path = Regex.Replace(path, @"[.:/,%?&=]", @"+", RegexOptions.IgnoreCase);
            path = Regex.Replace(path, @"[+]+", @"+", RegexOptions.IgnoreCase);
            
            return path;
        }
    }
}

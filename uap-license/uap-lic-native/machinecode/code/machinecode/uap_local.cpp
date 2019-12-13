// uap_local.cpp : 定义控制台应用程序的入口点。
//
#include <vector>
#include <string>
#include <fstream>

#include "MD5Encrypt.h"
#include "SM3Encrypt.h"

#ifdef __linux__
#ifndef _GNU_SOURCE
#define _GNU_SOURCE
#endif //_GNU_SOURCE

#include <cstdio>
#include <cerrno>
#include <unistd.h>
#include <cstdlib>
#include <cstring>

#include <dirent.h>
#include <fcntl.h>
#include <net/if.h>
#include <arpa/inet.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/socket.h>
#include <linux/major.h>
#include <sys/syscall.h>

#define WORD_EAX  0
#define WORD_EBX  1
#define WORD_ECX  2
#define WORD_EDX  3

#define WORD_NUM  4

#define USE_INSTRUCTION  (-2)

#define MAX_CPUS  1024

#define DEFAULT_CPUID "cpuId-0FABFBFF000306F20000000000000001"

#define HOST_ADDRESS "169.254.169.254"

//////////////////////////////////////////////////////////////////////////
//brief:访问机器的cpu信息文件，返回文件句柄
//param: cpu:循环访问cpu文件的次数（cpu文件名，查看路径/dev/cpu/便可知悉）
//return：读取cpu文件的句柄
//            -1：读取错误
//            -9:无权限访问
//tips:ENOENT:no such file
//        ENXIO: is a FIFO and no process has the file open for reading.  Or, the file is a device special  file  and  no corresponding device exists
//        ENODEV:device doesn't support that type
//        EACCES:can't access(permission denied)
//////////////////////////////////////////////////////////////////////////
static int RealSetup(const unsigned int  cpu){
    int    cpuid_fd = -1;
    char   cpuid_name[20];

    if (cpuid_fd == -1 && cpu == 0) {
        cpuid_fd = open("/dev/cpuid", O_RDONLY);
        if (cpuid_fd == -1 && errno != ENOENT) {
            //exit(1);
            return -9;
        }
    }

    if (cpuid_fd == -1) {
        sprintf(cpuid_name, "/dev/cpu/%u/cpuid", cpu);
        cpuid_fd = open(cpuid_name, O_RDONLY);
        if (cpuid_fd == -1) {
            if (cpu > 0 && (errno == ENXIO || errno == ENODEV)) {
                return -1;
            }
            if (errno != ENOENT) {
                return -9;
            }
        }
    }
    return cpuid_fd;
}

#define  PORT 80
#define  ARRAY_SIZE 2048


//////////////////////////////////////////////////////////////////////////
//brief:通过http获取json字符串
//param： hostAddr：主机地址
//            hostName:主机名称(一般与主机地址相同即可)
//            url：获取json的路径
//            returnValue：出参，接去获取到的json字符串
//return：    0：成功 -1：失败
//////////////////////////////////////////////////////////////////////////
int GetJsonText(const char* hostAddr, const char* hostName, const char* url, char* returnValue){
    int hSocket;
    struct sockaddr_in serv_Addr;
    char httpString[ARRAY_SIZE] = {0};

    hSocket = socket(PF_INET,SOCK_STREAM,0);
    if (hSocket < 0){
        return -1;
    }

    memset(&serv_Addr,0,sizeof(serv_Addr));
    serv_Addr.sin_family = AF_INET;
    serv_Addr.sin_addr.s_addr = inet_addr(hostAddr);
    serv_Addr.sin_port = htons(PORT);

    if (connect(hSocket,(struct sockaddr*)&serv_Addr,sizeof(serv_Addr)) < 0){
        return -1;
    }

    memset(httpString,0,ARRAY_SIZE);
    strcat(httpString,"GET ");
    strcat(httpString,url);
    strcat(httpString," HTTP/1.1\r\n");
    strcat(httpString,"Host: ");
    strcat(httpString,hostName);
    strcat(httpString,"\r\n");
    strcat(httpString,"Connection:Close\r\n");
    strcat(httpString,"Content-Type: text/html\r\n");
    strcat(httpString,"Metadata:true\r\n");
    strcat(httpString,"\r\n");

    int ret = write(hSocket,httpString,strlen(httpString));
    if (ret <= 0){
        return -1;
    }

    ret = read(hSocket,returnValue,ARRAY_SIZE);
    close(hSocket);
    if (ret <= 0){
        return -1;
    }

    return 0;
}

//////////////////////////////////////////////////////////////////////////
//brief:拆分字符串
//param： in：需要被拆分的字符串
//            splitSign:拆分字符串
//return：    vector<string>,被拆分的字符串的数组
//////////////////////////////////////////////////////////////////////////
std::vector<std::string> SplitInfoString(const char* in, const char splitSign){
    std::string res(in);

    if (res[res.size() -1] != splitSign){
        res += splitSign;
    }

    std::vector<std::string> splitVector;
    int pos = res.find(splitSign);
    int size = res.size();

    while(pos != std::string::npos){
        std::string _partString = res.substr(0,pos);
        if (!_partString.empty()){
            splitVector.push_back(_partString);
        }    
        res = res.substr(pos+1,size);
        pos = res.find(splitSign);
        size = res.size();
    }
    return splitVector;
}

//////////////////////////////////////////////////////////////////////////
//brief：获取实例ID
//param： host：主机地址
//            name:主机名（一般与主机地址相同）
//            url:官方提供的获取实例信息的地址
//            cpuIdStr:出参，用于接去实例Id字符串
//            cloudType：云环境类型，考虑到不同的云环境的实例信息json内容不同0：amazon、1：azure、2：华为云
//return：    0：成功 -1：失败
//////////////////////////////////////////////////////////////////////////
int GetInstanceId(const char* host,const char* name,const char* url,char* cpuIdStr,const int cloudType){
    char resultString[ARRAY_SIZE] = {0};


    int ret = GetJsonText(host,name,url,resultString);


    if (0 == ret && strstr(resultString,"200 OK")){
        char lastResult[256] = {0};

        if(cloudType == 0 || cloudType == 1){
            std::vector<std::string> jsonV = SplitInfoString(resultString,'\n');
            if(jsonV.size() > 0){

                memcpy(lastResult,jsonV[jsonV.size() -1].c_str(),jsonV[jsonV.size() -1 ].size());

                strcpy(cpuIdStr,"InstanceId-");

                strcat(cpuIdStr,lastResult);

                return 0;
            }
        }
        else if(cloudType == 2){
            std::vector<std::string> jsonV = SplitInfoString(resultString,',');
            std::string vpcIdString = "";
            for(int i = 0; i < jsonV.size(); i++){
                if(strstr(jsonV[i].c_str(),"vpc_id"))
                {
                    vpcIdString = jsonV[i];
                    break;
                }
            }
            if(!vpcIdString.empty()){
                return -1;
            }
            std::vector<std::string> vpcIdV = SplitInfoString((char *)vpcIdString.c_str(),'"');
            if(vpcIdV.size() > 0){
                memcpy(lastResult,vpcIdV[vpcIdV.size() -1].c_str(),vpcIdV[vpcIdV.size() -1 ].size());

                strcpy(cpuIdStr,"InstanceId-");

                strcat(cpuIdStr,lastResult);

                return 0;
            }
        }
    }
    return -1;
}
//////////////////////////////////////////////////////////////////////////
//brief:获取机器cpu ID
//param： cpuIdStr：出参，用于接取获取到的cpuid字符串
//return：0：成功、 -1：失败
//////////////////////////////////////////////////////////////////////////
int GetCpuId(char* cpuIdStr)
{
    unsigned int cpu;
    bool one_cpu = false;
    char* cpuIdCmp[50];
    int cmpCount = 1;

    if(NULL == cpuIdStr)
    {
        return -1;
    }

    for(cpu = 0; ;cpu++){
        int  cpuid_fd = -1;
        unsigned int   max;
        unsigned int   reg;

        if(one_cpu && cpu >0) break;

        cpuid_fd = RealSetup(cpu);

        if(cpuid_fd == -1){
            break;
        }

        if(cpuid_fd == -9){
            //char host[64] = "169.254.169.254";
            //char name[64] = "196.254.169.254";

            char url_amazon[64] = "/latest/meta-data/instance-id";
            int getInstanceId = GetInstanceId(HOST_ADDRESS, HOST_ADDRESS, url_amazon, cpuIdStr, 0);
            if(0 == getInstanceId){
                return 0;
            }

            char url_azure[128] = "/metadata/instance/compute/vmId?api-version=2019-06-01&format=text";
            getInstanceId = GetInstanceId(HOST_ADDRESS, HOST_ADDRESS, url_azure, cpuIdStr, 1);
            if(0 == getInstanceId){
                return 0;
            }

            char url_huawei[128] = "/openstack/latest/meta_data.json";
            getInstanceId = GetInstanceId(HOST_ADDRESS, HOST_ADDRESS, url_huawei, cpuIdStr,2);
            if(0 == getInstanceId){
                return 0;
            }

            strcpy(cpuIdStr,DEFAULT_CPUID);
            return 0;
        }

        char buf[512];
        unsigned int words[WORD_NUM];
        unsigned int words_3[WORD_NUM];

        if (cpuid_fd == USE_INSTRUCTION){
            asm("cpuid"
                : "=a" (words[WORD_EAX]),
                "=b" (words[WORD_EBX]),
                "=c" (words[WORD_ECX]),
                "=d" (words[WORD_EDX])
                : "a" (0), 
                "c" (0));
        } 
        else {
            //offset 1
            off64_t  result;
            off64_t  offset = ((off64_t)0 << 32) + 1;
            int status;

            result = lseek64(cpuid_fd, offset, SEEK_SET);

            if (result == -1) {
                strcpy(cpuIdStr,DEFAULT_CPUID);
                close(cpuid_fd);
                return 0;
            }

            status = read(cpuid_fd, words, 16);
            if (status == -1) {
                strcpy(cpuIdStr,DEFAULT_CPUID);
                close(cpuid_fd);
                return 0;
            } 
            //offset 3
            offset = ((off64_t)0 << 32) + 3;

            result = lseek64(cpuid_fd, offset, SEEK_SET);
            if (result == -1) {
                strcpy(cpuIdStr,DEFAULT_CPUID);
                close(cpuid_fd);
                return 0;
            }

            status = read(cpuid_fd, words_3, 16);
            if (status == -1) {
                strcpy(cpuIdStr,DEFAULT_CPUID);
                close(cpuid_fd);
                return 0;
            } 
        }
        sprintf(buf,"%08X%08X%08X%08X",words[WORD_EDX],words[WORD_EAX],words_3[WORD_EDX],words_3[WORD_ECX]);

        if (0 == cpu){
            strcpy(cpuIdStr,"cpuId-");
            cpuIdCmp[0] = buf;
        }
        else{
            int found = 0;
            int j;

            for (j=0;j<cmpCount && 0 != strcmp(buf,cpuIdCmp[j]);j++){}
            if (j < cmpCount){
                close(cpuid_fd);
                continue;
            }

            cpuIdCmp[cmpCount] = buf;
            cmpCount++;

            strcat(cpuIdStr,"cpuId-");
        }
        strcat(cpuIdStr,buf);

        close(cpuid_fd);
    }
    if(0 == cpu){
        strcpy(cpuIdStr,DEFAULT_CPUID);    
    }

    return 0;
}

//////////////////////////////////////////////////////////////////////////
//brief:获取文件夹下的子文件夹名称
//param： childDir：出参，用于接取获取到的子文件夹名称
//            filePath：路径
//////////////////////////////////////////////////////////////////////////
void GetChildDirName(std::vector<std::string> &childDir,const char* filePath)
{
    DIR *dirp; 
    struct dirent *dp;
    int i = 0;
    dirp = opendir(filePath);
    if (NULL == dirp){
        return;
    }
    while ((dp = readdir(dirp)) != NULL) {
        childDir.push_back(std::string(dp->d_name));
        i++;
    }      
    closedir(dirp);
}
//////////////////////////////////////////////////////////////////////////
//brief:是否是虚拟网卡
//param： nameArray：所有虚拟网卡的名称
//            name：该网卡的名称
//////////////////////////////////////////////////////////////////////////
bool IsVirtualNetworkCard(const std::vector<std::string> nameArray,const char* name)
{
    int i;
    for (i = 0;i<nameArray.size();i++){
        if (!strcmp(nameArray[i].c_str(),name)){
            return true;
        }
    }
    return false;
}
//////////////////////////////////////////////////////////////////////////
//brief:是否是绑定网卡
//param：name：该网卡的名称
//////////////////////////////////////////////////////////////////////////
bool IsBonding(const char* name)
{
    std::vector<std::string> childDir;

    GetChildDirName(childDir,"/proc/net/bonding");

    for (int j = 0;j < childDir.size();j++){
        if (0 ==strcmp(name,childDir[j].c_str())){
            return true;
        }
    }
    return false;
}

//////////////////////////////////////////////////////////////////////////
//brief:读取绑定网卡的真实mac地址
//param： bondName：绑定网卡的名称
//            mac：出参，用于接取获取到的mac地址
//            count：第几次获取网卡地址
//return：0：成功、 -1：失败
//////////////////////////////////////////////////////////////////////////
int ReadBondingMac(const char* bondName,char*mac,int count)
{
    char fileName[] = "/proc/net/bonding/";
    strcat(fileName,bondName);

    FILE *fp;
    char strLine[1024];
    if((fp = fopen(fileName,"r")) == NULL){
        return -1;
    }

    while(!feof(fp)){
        fgets(strLine,1024,fp);
        if (0 == strncmp(strLine,"Permanent HW addr:",18)){
            if (1 == count){
                strcpy(mac,"Mac-");
            }
            else{
                strcat(mac,"Mac-");
            }
            strncat(mac,strLine+19,17);
            count++;
        }
    }
    fclose(fp);
    return 0;
}
//////////////////////////////////////////////////////////////////////////
//brief:获取mac地址
//param： mac：出参，用于接取获取到的mac地址字符串
//return：0：成功、 -1：失败
//////////////////////////////////////////////////////////////////////////
int GetLocalMAC(char* mac)
{
    struct ifreq ifr;
    struct ifconf ifc;
    char buf[2048];
    int success = 0;

    int sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_IP);

    std::vector<std::string> dirName;
    GetChildDirName(dirName,"/sys/devices/virtual/net");
    if (sock == -1) {
        return -1;
    }

    ifc.ifc_len = sizeof(buf);
    ifc.ifc_buf = buf;
    if (ioctl(sock, SIOCGIFCONF, &ifc) == -1) {
        return -1;
    }

    struct ifreq* it = ifc.ifc_req;
    const struct ifreq* const end = it + (ifc.ifc_len / sizeof(struct ifreq));
    char szMac[64];
    int count = 0;
    int cc = 0;
    for (; it != end; ++it) {
        cc++;
        strcpy(ifr.ifr_name, it->ifr_name);
        if (ioctl(sock, SIOCGIFFLAGS, &ifr) != 0) {
            return -1;
        }
        if ((ifr.ifr_flags & IFF_LOOPBACK) || ioctl(sock, SIOCGIFHWADDR, &ifr) != 0) {
            continue;
        }

        unsigned char * ptr ;
        ptr = (unsigned char *)&ifr.ifr_ifru.ifru_hwaddr.sa_data[0];

        if (!IsVirtualNetworkCard(dirName,ifr.ifr_name)){
            count ++ ;
            snprintf(szMac,64,"%02X:%02X:%02X:%02X:%02X:%02X",*ptr,*(ptr+1),*(ptr+2),*(ptr+3),*(ptr+4),*(ptr+5));    

            if (1 == count){
                strcpy(mac,"Mac-");
            }
            else{
                strcat(mac,"Mac-");
            }
            strcat(mac,szMac);

            continue;
        }
        if(IsBonding(ifr.ifr_name)){
            count++;
            ReadBondingMac(ifr.ifr_name,mac,count);
        }
    }
    close(sock);
    return 0;
}


#else
#include <WinSock2.h>
#include <Windows.h>
#include <intrin.h>
#include <stdio.h>
#include <stdlib.h>
#include <WinBase.h>

#include <IPHlpApi.h>
#pragma comment(lib,"iphlpapi.lib")

void SetCPU(HANDLE h,int cpuNo)
{
    DWORD_PTR processAffinity;
    DWORD_PTR systemAffinity;
    BOOL gpa,spa;
    gpa = GetProcessAffinityMask(h,&processAffinity,&systemAffinity);
    processAffinity = (DWORD)cpuNo;
    spa = SetProcessAffinityMask(h,processAffinity);

}

#if _MSC_VER < 1600    // no __cpuidex before VC2008 SP1  
void __cpuidex(INT32 CPUInfo[4], INT32 InfoType, INT32 ECXValue) {
    if (NULL==CPUInfo){
        return;
    }
    _asm{
        //load
        mov edi, CPUInfo;
        mov eax, InfoType;
        mov ecx, ECXValue;
        // CPUID
        cpuid;
        // save.
        mov    [edi], eax;
        mov    [edi+4], ebx;
        mov    [edi+8], ecx;
        mov    [edi+12], edx;
    }
}
#endif

int GetCpuId(char* cpuIdStr)
{
    SYSTEM_INFO sysInfo;
    DWORD cpuCount;
    int i = 0,j,k=1;
    int foundSame = 0;
    char* cpuid_str_cmp[16];

    GetSystemInfo(&sysInfo);
    cpuCount = sysInfo.dwNumberOfProcessors;

    for (i= 1; i<=(int)cpuCount;i++){
        HANDLE _currentProcess = GetCurrentProcess();
        char cpuid_str[512] = "";
        INT32 dwBuf[4] = {0};
        SetCPU(GetCurrentProcess(),i);
        __cpuidex(dwBuf, 1, 1);
        sprintf(cpuid_str, "%08X%08X", dwBuf[3], dwBuf[0]);

        if (1 == i){
            cpuid_str_cmp[0] = cpuid_str;
        }
        else{
            foundSame = 0;
            for (j=0;j<16;j++){
                if (0 == strcmp(cpuid_str,cpuid_str_cmp[j])){
                    foundSame = 1;
                    break;
                }
            }
            if (1 == foundSame){
                continue;
            }
        }
        cpuid_str_cmp[k] = cpuid_str;
        k++;
        if (1 == i){
            strcpy(cpuIdStr,"cpuId-");
        }
        else{
            strcat(cpuIdStr,"cpuId-");
        }
        strcat(cpuIdStr,cpuid_str);
    }
    strcat(cpuIdStr,",");
    return 0;
}

void byteToHex(unsigned char bData,char hex[])
{
    int high = bData/16;
    int low = bData%16;
    hex[0] = (high<10)?('0'+high):('A'+high-10);
    hex[1] = (low<10)?('0'+low):('A'+low-10);
}

int GetLocalMAC(char* mac)
{
    ULONG ulSize=0;
    PIP_ADAPTER_INFO pInfo=NULL;
    int temp=0;
    int iCount=0;
    temp = GetAdaptersInfo(pInfo,&ulSize);
    pInfo=(PIP_ADAPTER_INFO)malloc(ulSize);
    temp = GetAdaptersInfo(pInfo,&ulSize);

    int countpi = 0;
    while(pInfo){
        memcpy(mac + iCount,"MAC-",4);
        iCount+=4;
        int i;
        //  pInfo->Address MAC
        for(i=0;i<(int)pInfo->AddressLength;i++){
            byteToHex(pInfo->Address[i],&mac[iCount]);
            iCount+=2;
            if(i<(int)pInfo->AddressLength-1){
                mac[iCount++] = ':';
            }else{
                mac[iCount++] = '#';
            }
        }
        pInfo = pInfo->Next;
    }

    if(iCount >0){
        mac[--iCount]='\0';
        return iCount;
    }
    else return -1;
}

#endif

std::string EncryptBySM3MD5(char* ori,int len){
    //sm3
    Bit8 buf[SM3_DIGEST_LENGTH] = { 0 };
    Bit8 *res = new Bit8[len+1];
    memset(res,0,len+1);
    memcpy(res,ori,len);
    SM3(res, len, buf);
    delete[] res;
    res = NULL;
    char sm3ResultString[1024] = "";
    for(Bit32 i = 0; i < SM3_DIGEST_LENGTH; i ++){
        char midStr[32] = "";
        sprintf(midStr,"%02X", (unsigned char)buf[i]);
        strcat(sm3ResultString,midStr);
    }
    //printf("after 3:%s\n",sm3ResultString);
    //MD5 test
    char szDigest[16] = "";
    char md5ResultString[512] = "";
    char md5Res[65] = "";
    memset(md5Res,0,65);
    memcpy(md5Res,sm3ResultString,64);
    MD5Digest(md5Res, 64, szDigest);
    int i;
    for (i = 0; i < 16; i++){
        char midStr[32] = "";
        sprintf(midStr,"%02X", (unsigned char)szDigest[i]);
        strcat(md5ResultString,midStr);
    }
    //printf("after 5:%s\n",md5ResultString);
    std::string resultString(md5ResultString);
    return resultString;
}

int main(int argc, char* argv[]){
    //get cpuid and mac
    char _cpuId[1024];
    char _address[1024];
    GetCpuId(_cpuId);
    if(GetLocalMAC(_address) >= 0){
        strcat(_cpuId,_address);
    }
    //printf("%s\n",_cpuId);
    //do encrypt
    strcat(_cpuId,"0");
    std::string resultStr(_cpuId);
    resultStr = EncryptBySM3MD5((char*)resultStr.c_str(),resultStr.length());
    printf("machine code:%s\n",resultStr.c_str());
    //write into file
    std::ofstream outFile;
    outFile.open("MachineCode.txt");
    outFile << resultStr.c_str() << std::endl;
    outFile.close();
    //end
#ifdef WIN32
    system("pause");
#endif
    return 0;
}



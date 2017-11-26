package org.b0102.spring.boot.pkg.repair

import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

import org.apache.commons.io.IOUtils
import java.util.zip.ZipOutputStream
import java.io.FileOutputStream
import java.util.zip.Deflater
import java.util.TreeMap

object Main 
{
  private class ZipDataBean(val name:String, val size:Long, val crc:Long, val data:Array[Byte] ) extends Serializable with Comparable[ZipDataBean]
  {
    private def this() = this(null, 0, 0, null)
    override def compareTo(zip:ZipDataBean):Int = name.compareTo(zip.name)
  }
  
  def showHelp():Unit =
  {
    println("A utility for repacking modified spring boot executable")
    println("Author: bugbug0102")
    println
    println(s"Usage: [input jar] [output jar]")
  }
  
  def main(args:Array[String]):Unit =
  {
    if(args.length >= 2)
    {
      val in = new File(args(0))
      val out = new File(args(1))
      
      var fis:FileInputStream = null
      var zis:ZipInputStream  = null
      val zds = try
      {
        fis = new FileInputStream(in)
        zis = new ZipInputStream(fis)
        val ret = new collection.mutable.ListBuffer[ZipDataBean]()
        
        var ze:ZipEntry = zis.getNextEntry
        while(ze!=null)
        {
          val zd = new ZipDataBean(ze.getName, ze.getSize, ze.getCrc, IOUtils.toByteArray(zis))
          ze = zis.getNextEntry
          ret += zd
        }
        ret
      
      }finally
      {
        IOUtils.closeQuietly(zis)
        IOUtils.closeQuietly(fis)
        List()
      }
      
      var fos:FileOutputStream = null
      var zos:ZipOutputStream = null
      try
      {
        fos = new FileOutputStream(out)
        zos = new ZipOutputStream(fos)
        
        zds.sorted.foreach{ zd=>
          val nze = new ZipEntry(zd.name)
          if(zd.size > 0)
          {
            nze.setMethod(ZipOutputStream.STORED)
            nze.setCrc(zd.crc)
            nze.setSize(zd.size)
          }
          println(s"Working:\t ${zd.name}")
          zos.putNextEntry(nze)
          zos.write(zd.data)
          zos.closeEntry()
        }
        
      }finally
      {
        IOUtils.closeQuietly(zos)
        IOUtils.closeQuietly(fos)
      }
      
      println("END") 
    }else
    {
      showHelp 
    }
  }
  
}
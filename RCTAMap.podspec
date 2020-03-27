require "json"
version = JSON.parse(File.read("package.json"))["version"]

Pod::Spec.new do |spec|

  spec.name         = "RCTAMap"
  spec.version      = version
  spec.summary      = "A short description of RCTAMap."
  spec.homepage     = "https://github.com/dianwoba/react-native-amap"
  spec.license      = "MIT"
  spec.author             = { "wwwlin" => "188658587@qq.com" }
  spec.ios.deployment_target = "9.0"
  spec.tvos.deployment_target = "9.0"
  spec.source         = { :git => 'https://github.com/dianwoba/react-native-amap.git', :tag => "v#{spec.version}"}
  spec.source_files  =  "ios/**/*.{h,m}"

  spec.requires_arc = true

  spec.dependency "React"
  spec.dependency "AMapFoundation-NO-IDFA"
  spec.dependency "AMap2DMap-NO-IDFA"
  spec.dependency "AMapSearch-NO-IDFA"
end

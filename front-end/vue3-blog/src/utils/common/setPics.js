export const getDefaultArticleThumbnail=(article)=>{
  console.log(article)
  if(article.articleThumbnail){
    return `/uploaded-images/${article.articleThumbnail}`
  }else{
    return new URL('@/assets/default/pics/1ad50b1598ddec725db64cabcf27ecc.jpg', import.meta.url).href
  }
}

export const getDefaultUserAvatar=(user)=>{
  if(user&&user.userAvatar&& typeof user.userAvatar === 'string' && user.userAvatar.trim()){
    return `/uploaded-images/${user.userAvatar}`
  }else{
    return new URL('@/assets/default/pics/blog_avatar2.png', import.meta.url).href
  }
}


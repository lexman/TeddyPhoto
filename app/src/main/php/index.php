<?php

require __DIR__.'/Router.php';
$router = new \Delight\Router\Router();

function this_url() {
    $protocol = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? "https" : "http");
    return  "$protocol://$_SERVER[HTTP_HOST]$_SERVER[REQUEST_URI]";
}
function compute_root_url() {
    $protocol = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? "https" : "http");
    return  "$protocol://$_SERVER[HTTP_HOST]";
}

function remove_dot_slash($path) {
    return substr($path, 2);
}

function file_last_modif($path) {
    return stat($path)['atime'];
}

function list_album($album) {
    $root_photos_url = $_SERVER['HTTP_HOST'] . '/albums/'.$album.'/';
    $root_url = compute_root_url() . "/";

    $pics_path = './albums/'.$album.'/*.jpg';
    $files = glob($pics_path);
    $res = [];
    foreach($files as $file) {
        $elemt = array(
            "url" => $root_url . remove_dot_slash($file),
            "thumb_url" => $root_url . remove_dot_slash($file),
            "ts" => file_last_modif($file)
        );
        $res[] = $elemt;
    }
    return json_encode($res);
}

$router->get('/albums/:album/', function ($album) {
    if ($album == "album1") {
        print(list_album($album));
    }
    print($album);
});

function post_image($album) {
    if ($album == "album1") {        
        $body = file_get_contents('php://input');
        $file = "".time().".jpg";
        $pics_path = './albums/'.$album . '/';
        file_put_contents($pics_path . $file, $body);
        
        $root_url = compute_root_url() . "/";

        $pic = array(
            "url" => $root_url . $file,
            "thumb_url" => $root_url . $file,
            "ts" => file_last_modif($pics_path . $file)
        );
        return json_encode($pic);
    }
    return null;
}

$router->post('/albums/:album/', function ($album) {
    $res = null;
    if ($album == "album1") {
        $res = post_image($album);
    }
    if ($res != null) {
        print($res);
    } else {
        // 400 !
    } 
});

?>


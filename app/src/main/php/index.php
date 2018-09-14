<?php

require __DIR__.'/Router.php';
$router = new \Delight\Router\Router();

function this_url() {
    $protocol = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? "https" : "http");
    $port = (isset($_SERVER['SERVER_PORT']) && $_SERVER['SERVER_PORT'] != 80 ? ":$_SERVER[SERVER_PORT]" : "");
    return  "$protocol://$_SERVER[HTTP_HOST]$port$_SERVER[REQUEST_URI]";
}

function remove_dot_slash($path) {
    return substr($path, 2);
}

function file_last_modif($path) {
    return stat($path)['atime'];
}

function list_album($album) {
    $root_photos_url = $_SERVER['HTTP_HOST'] . '/albums/'.$album.'/';
    $root_url = this_url();

    $pics_path = './albums/'.$album.'/*.jpg';
    $files = glob($pics_path);
    $res = [];
    foreach($files as $file ) {
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

// phpinfo();
?>


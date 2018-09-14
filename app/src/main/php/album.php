<?php

$root_url = $_SERVER['HTTP_HOST'] . $_SERVER['REQUEST_URI'];

$pics_path = './albums/album1/*.jpg';

$root_photos_url = $_SERVER['HTTP_HOST'] . '/albums/album1/';


$files = glob($pics_path);

$res = [];

function remove_dot_slash($path) {
    return substr($path, 2);
}

function file_ast_modif($path) {
    return stat($path)['atime'];
}

foreach($files as $file ) {
    $elemt = array(
        "url" => $root_url . remove_dot_slash($file),
        "thumb_url" => $root_url . remove_dot_slash($file),
        "ts" => file_ast_modif($file)
    );
    $res[] = $elemt;

}


/*

for($i = 1; $i < 100; $i++) {
    $elemt = array(
        "url" => "photos/" . $i . ".jpg",
        "foo" => "bar"
    );
    $res[] = $elemt;
}*/

print(json_encode($res));

phpinfo();
?>test

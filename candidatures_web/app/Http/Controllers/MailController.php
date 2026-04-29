<?php

namespace App\Http\Controllers;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Mail;
use App\Mail\TestMail;

class MailController extends Controller
{
    public function envoyerMail(Request $request)
    {
        $address = $request->input('email');
        $subject = $request->input('subject');
        $content = $request->input('content');

        if (!$address || !$subject || !$content) {
            return response()->json([
                'error' => 'Paramètres manquants',
                'success' => false
            ], 400);
        }

        Mail::to($address)->send(new TestMail($subject, $content));

        return response()->json([
            'message' => 'Mail envoyé',
            'success' => true
        ]);
    }
}